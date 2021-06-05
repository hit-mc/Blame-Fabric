package com.keuin.blame.lookup;

import com.keuin.blame.data.entry.LogEntry;
import com.keuin.blame.data.entry.LogEntryNames;
import com.keuin.blame.util.DatabaseUtil;
import com.mongodb.client.*;
import com.mongodb.client.model.Sorts;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import static com.keuin.blame.util.DatabaseUtil.CLIENT_SETTINGS;

public class LookupWorker extends Thread {

    private final Logger logger;
    private final BlockingQueue<LookupFilterWithCallback> queue;
    private boolean running = true;

    public LookupWorker(int id, BlockingQueue<LookupFilterWithCallback> queue) {
        this.queue = queue;
        this.logger = Logger.getLogger(String.format("LookupWorker-%d", id));
    }

    public void disable() {
        interrupt();
        running = false;
    }

    @Override
    public void run() {
        try (final MongoClient mongoClient = MongoClients.create(CLIENT_SETTINGS)) {
            final MongoDatabase db = mongoClient.getDatabase(
                    DatabaseUtil.MONGO_CONFIG.getDatabaseName()
            );
            final MongoCollection<LogEntry> collection = db.getCollection(
                    DatabaseUtil.MONGO_CONFIG.getLogCollectionName(), LogEntry.class
            );
            long time;
            while (running) {
                LookupFilterWithCallback item = queue.take();
                LookupCallback callback = item.getCallback();
                AbstractLookupFilter filter = item.getFilter();

                time = System.currentTimeMillis();
                FindIterable<LogEntry> find = collection
                        .find()
                        .filter(filter.filter())
                        .sort(Sorts.descending(LogEntryNames.TIMESTAMP_MILLIS))
                        .limit(item.getLimit());
//                FindIterable<LogEntry> find = collection.find();//.sort(Sorts.descending("timestamp_millis"));
                time = System.currentTimeMillis() - time;
                logger.info(String.format("Lookup finished in %d ms.", time));
                callback.onLookupFinishes(find);
            }
        } catch (InterruptedException e) {
            logger.info("Interrupted. Quitting...");
        }
    }
}
