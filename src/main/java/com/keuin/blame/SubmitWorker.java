package com.keuin.blame;

import com.keuin.blame.data.entry.LogEntry;
import com.keuin.blame.util.DatabaseUtil;
import com.mongodb.MongoClientException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class SubmitWorker {

    public static final SubmitWorker INSTANCE = new SubmitWorker();
    private final Logger logger = Logger.getLogger(SubmitWorker.class.getName());

    private final BlockingQueue<LogEntry> queue = new ArrayBlockingQueue<>(1048576);
    private final Thread thread = new Thread(SubmitWorker.this::run);

    private final AtomicBoolean isStopped = new AtomicBoolean(false);


    private SubmitWorker() {
        thread.setUncaughtExceptionHandler((t, e) -> logger.severe(String.format("Exception in thread %s: %s", t.getName(), e)));
        thread.start();
    }

    public void submit(LogEntry entry) {
        if (isStopped.get()) {
            return;
        }
        if (entry == null)
            throw new IllegalArgumentException("entry cannot be null");
        if (!queue.offer(entry)) {
            logger.severe("Write queue is full. Dropping new log entries.");
        }
    }

    public void stop() {
        isStopped.set(true);
        thread.interrupt();
    }

    private void run() {
        try (final MongoClient mongoClient = MongoClients.create(DatabaseUtil.CLIENT_SETTINGS)) {
            final MongoDatabase db = mongoClient.getDatabase(
                    DatabaseUtil.MONGO_CONFIG.getDatabaseName()
            );
            final MongoCollection<LogEntry> collection = db.getCollection(
                    DatabaseUtil.MONGO_CONFIG.getLogCollectionName(), LogEntry.class
            );
            // TODO: 第一个事件触发导致延迟很大
            while (!isStopped.get() || !queue.isEmpty()) {
                try {
                    LogEntry entry = queue.take();
                    collection.insertOne(entry);
                } catch (InterruptedException ex) {
                    isStopped.set(true);
                }
            }
        } catch (MongoClientException exception) {
            logger.severe("Failed to submit: " + exception + ". Worker is quitting...");
        }
    }

}
