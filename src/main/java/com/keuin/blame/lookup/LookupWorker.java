package com.keuin.blame.lookup;

import com.keuin.blame.Blame;
import com.keuin.blame.config.MongoConfig;
import com.keuin.blame.data.LogEntry;
import com.keuin.blame.data.enums.codec.ActionTypeCodec;
import com.keuin.blame.data.enums.codec.ObjectTypeCodec;
import com.keuin.blame.data.enums.codec.WorldPosCodec;
import com.keuin.blame.util.DatabaseUtil;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

public class LookupWorker extends Thread {

    private final Logger logger;
    private final BlockingQueue<LookupFilterWithCallback> queue;
    private boolean running = true;

    private static final MongoConfig MONGO_CONFIG = Blame.config.getMongoConfig();
    private static final CodecRegistry CODEC_REGISTRY = CodecRegistries.fromRegistries(
            com.mongodb.MongoClient.getDefaultCodecRegistry(),
            CodecRegistries.fromCodecs(
                    new ActionTypeCodec(),
                    new ObjectTypeCodec(),
                    new WorldPosCodec()
//                    new LogEntryCodec()
            ),
            fromProviders(PojoCodecProvider.builder().automatic(true).build())
    );
    private static final MongoClientSettings CLIENT_SETTINGS = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(MONGO_CONFIG.getAddress()))
            .codecRegistry(CODEC_REGISTRY)
            .build();

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
//                FindIterable<LogEntry> find = filter.find(
//                        collection.find().sort(Sorts.descending("timestamp_millis"))
//                );
                FindIterable<LogEntry> find = collection.find();//.sort(Sorts.descending("timestamp_millis"));
                time = System.currentTimeMillis() - time;
                logger.info(String.format("Lookup finished in %d ms.", time));
                callback.onLookupFinishes(find);
            }
        } catch (InterruptedException e) {
            logger.info("Interrupted. Quitting...");
        }
    }
}
