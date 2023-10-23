package com.keuin.blame;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseException;
import com.clickhouse.client.ClickHouseRequest;
import com.clickhouse.client.ClickHouseResponse;
import com.clickhouse.data.ClickHouseDataStreamFactory;
import com.clickhouse.data.ClickHouseFormat;
import com.keuin.blame.data.entry.LogEntry;
import com.keuin.blame.util.DatabaseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SubmitWorker {

    public static final SubmitWorker INSTANCE = new SubmitWorker();
    private final Logger logger = LogManager.getLogger(SubmitWorker.class);
    private final BlockingQueue<LogEntry> queue = new ArrayBlockingQueue<>(1048576);
    private final Thread thread = new Thread(SubmitWorker.this::run);
    private final AtomicBoolean isStopped = new AtomicBoolean(false);

    private static final int batchSize = 1024;
    private static final int maxWaitMillis = 3000;

    private static final int idleMaxWaitMillis = 600 * 1000;

    private final AtomicInteger playerCounter = new AtomicInteger();
    private final AtomicBoolean isPlayerPresent = new AtomicBoolean(false);

    private final AtomicBoolean playerJoin = new AtomicBoolean(false);

    public void playerJoin() {
        if (isStopped.get()) return;
        playerCounter.incrementAndGet();
        playerJoin.set(true);
        isPlayerPresent.set(true);
        thread.interrupt(); // interrupt if sleeping
    }

    public void playerQuit() {
        var cnt = playerCounter.decrementAndGet();
        if (cnt == 0) {
            isPlayerPresent.set(false);
        }
    }


    private SubmitWorker() {
        thread.setUncaughtExceptionHandler((t, e) ->
                logger.error(String.format("Exception in thread %s: %s", t.getName(), e)));
        thread.setName(SubmitWorker.class.getSimpleName());
        thread.start();
    }

    public void submit(LogEntry entry) {
        if (isStopped.get()) {
            return;
        }
        if (entry == null)
            throw new IllegalArgumentException("entry cannot be null");
        if (!queue.offer(entry)) {
            logger.error("Write queue is full. Dropping new log entries.");
        }
    }

    public void stop() {
        isStopped.set(true);
        thread.interrupt();
    }

    private long getSleepDuration() {
        return isPlayerPresent.get() ? maxWaitMillis : idleMaxWaitMillis;
    }

    /**
     * Ensures the buffer is ready to consume from.
     *
     * @throws InterruptedException The buffer may be empty or non-empty.
     */
    private void accumulateBuffer(List<LogEntry> buffer) throws InterruptedException {
        long accumulateStart = -1;
        while (true) {
            if (
                    buffer.size() >= batchSize ||
                            (accumulateStart > 0 && System.currentTimeMillis() - accumulateStart > getSleepDuration())
            ) {
                // buffer is full, or max accumulate time reached, flush it
                break;
            }
            var el = queue.poll();
            if (el == null) {
                if (buffer.isEmpty()) {
                    // block until the first entry is read
                    el = queue.take();
                } else {
                    // try to read more entries with timeout
                    var duration = getSleepDuration();
                    logger.info("Sleep duration: " + duration);
                    el = queue.poll(duration, TimeUnit.MILLISECONDS);
                }
            }
            if (el == null) {
                // poll timed out, flush buffer
                break;
            }
            buffer.add(el);
            if (accumulateStart < 0) {
                accumulateStart = System.currentTimeMillis();
            }
        }
    }

    /**
     * Bulk write all entries in buffer to database.
     * Throws exception if error. The buffer is kept intact. No item is written.
     */
    private void bulkWrite(
            List<LogEntry> buffer,
            ClickHouseRequest.Mutation req
    ) throws IOException, ClickHouseException, ExecutionException, InterruptedException {
        if (buffer.isEmpty()) {
            logger.error("bulkWrite is called with empty write buffer");
            return;
        }
        logger.info("bulkWrite size: " + buffer.size());
        CompletableFuture<ClickHouseResponse> fut;
        try (var os = ClickHouseDataStreamFactory.getInstance()
                .createPipedOutputStream(req.getConfig())) {
            fut = req.data(os.getInputStream()).execute();
            for (var el : buffer) {
                el.write(os);
            }
        }
        try (var resp = fut.get()) {
            var summary = resp.getSummary();
            var expected = buffer.size();
            var actual = summary.getReadRows();
            logger.info("Write success: " + summary.toString());
            if (expected != actual) {
                logger.error(String.format(
                        "Unexpected write rows, expected %d (buffer), actual %d (write summary)",
                        expected, actual));
            }
        }
        buffer.clear();
    }

    private void run() {
        try {
            logger.info("ClickHouse writer thread started.");
            doRun();
        } finally {
            logger.info("ClickHouse writer thread stopped.");
        }
    }

    private void doRun() {
        var server = DatabaseUtil.getServer();
        var batchBuffer = new ArrayList<LogEntry>(batchSize);
        boolean writeImmediately = false;
        workLoop:
        while (true) {
            try (var client = DatabaseUtil.getClient(server)) {
                writeLoop:
                while (true) {
                    var req = client.read(server).write()
                            .table(DatabaseUtil.DB_CONFIG.getTable())
                            .format(ClickHouseFormat.RowBinary)
//                            .option(ClickHouseClientOption.ASYNC, false)
                            ;
                    var result = work(client, req, batchBuffer, writeImmediately);
                    switch (result) {
                        case CONTINUE -> {
                            if (isStopped.get()) {
                                return;
                            }
                            writeImmediately = false;
                        }
                        case RECONNECT -> {
                            writeImmediately = true;
                            logger.info("Reconnecting to ClickHouse...");
                            break writeLoop;
                        }
                        case FINISH -> {
                            break workLoop;
                        }
                        case INSTANT_WRITE -> {
                            writeImmediately = true;
                        }
                    }
                }
            }
        }
    }

    enum WorkResult {
        CONTINUE,
        RECONNECT,
        FINISH,
        INSTANT_WRITE
    }

    private @NotNull WorkResult work(
            ClickHouseClient client,
            ClickHouseRequest.Mutation req,
            List<LogEntry> buffer,
            boolean writeImmediately
    ) {
        boolean interrupted = false;
        // if writeImmediately is set, do not accumulate the buffer, flush it immediately
        try {
            // accumulate buffer
            if (!writeImmediately) {
                accumulateBuffer(buffer);
            }
        } catch (InterruptedException ignored) {
            // check if the interruption is triggered by player join
            // in this case, we don't stop, but try to write the buffer immediately
            if (!playerJoin.getAndSet(false)) {
                // server is closing, flush the buffer immediately
                // decline new write requests
                isStopped.set(true);
                interrupted = true;
            }
        }
        try {
            bulkWrite(buffer, req);
        } catch (IOException ignored) {
            // write failed
            if (interrupted) {
                // do not retry if already interrupted
                // the error may be unrecoverable
                return WorkResult.FINISH;
            }
            return WorkResult.RECONNECT;
        } catch (ClickHouseException | ExecutionException ex) {
            logger.error("ClickHouse writer error", ex);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }
            return WorkResult.RECONNECT;
        } catch (InterruptedException e) {
            return WorkResult.INSTANT_WRITE;
        }
        return WorkResult.CONTINUE;
    }

}
