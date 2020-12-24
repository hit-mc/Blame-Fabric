package com.keuin.blame;

import com.keuin.blame.data.LogEntry;
import com.keuin.blame.util.DatabaseUtil;
import com.mongodb.MongoClientException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

public class SubmitWorker {

    public static final SubmitWorker INSTANCE = new SubmitWorker();
    private final Logger logger = Logger.getLogger(SubmitWorker.class.getName());

    private final BlockingQueue<LogEntry> queue = new LinkedBlockingDeque<>(4096);
    private final Thread thread = new Thread(SubmitWorker.this::run);
    private boolean run = true;


    private SubmitWorker() {
        thread.setUncaughtExceptionHandler((t, e) -> logger.severe(String.format("Exception in thread %s: %s", t.getName(), e)));
        thread.start();
    }

    public void submit(LogEntry entry) {
        if (entry == null)
            throw new IllegalArgumentException("entry cannot be null");
        queue.offer(entry);
    }

    public void stop() {
        thread.interrupt();
        this.run = false;
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
            while (this.run) {
                LogEntry entry = queue.take();
                collection.insertOne(entry);
                logger.info("Entry inserted.");
            }
        } catch (InterruptedException ignored) {
        } catch (MongoClientException exception) {
            logger.severe("Failed to submit: " + exception + ". Worker is quitting...");
        }
    }

}
