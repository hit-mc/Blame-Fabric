package com.keuin.blame;

import com.keuin.blame.config.MongoConfig;
import com.keuin.blame.data.LogEntry;
import com.keuin.blame.data.enums.ActionType;
import com.keuin.blame.data.enums.ObjectType;
import com.keuin.blame.data.enums.codec.ActionTypeCodec;
import com.keuin.blame.data.enums.codec.ObjectTypeCodec;
import com.keuin.blame.data.enums.transformer.ActionTypeTransformer;
import com.keuin.blame.data.enums.transformer.ObjectTypeTransformer;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BSON;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

public class SubmitWorker {

    public static final SubmitWorker INSTANCE = new SubmitWorker(Blame.config.getMongoConfig());
    private final Logger logger = Logger.getLogger(SubmitWorker.class.getName());

    private final BlockingQueue<LogEntry> queue = new LinkedBlockingDeque<>(4096);
    private final Thread thread = new Thread(SubmitWorker.this::run);
    private boolean run = true;

    private final MongoConfig mongoConfig;
    private final MongoClientSettings settings;


    private SubmitWorker(MongoConfig mongoConfig) {
        if (mongoConfig == null)
            throw new IllegalArgumentException("mongo config cannot be null");
        this.mongoConfig = mongoConfig;
        logger.fine("Connecting to MongoDB server `" + mongoConfig.getAddress()
                + "` with database `" + mongoConfig.getDatabaseName()
                + "` and collection `" + mongoConfig.getLogCollectionName() + "`.");

        BSON.addEncodingHook(ActionType.class, new ActionTypeTransformer());
        BSON.addEncodingHook(ObjectType.class, new ObjectTypeTransformer());

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                com.mongodb.MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs(new ActionTypeCodec(), new ObjectTypeCodec())
        );

        settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoConfig.getAddress()))
                .codecRegistry(codecRegistry)
                .build();
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
        try (final MongoClient mongoClient = MongoClients.create(settings)) {
            final MongoDatabase db = mongoClient.getDatabase(
                    mongoConfig.getDatabaseName()
            );
            final MongoCollection<LogEntry> collection = db.getCollection(
                    mongoConfig.getLogCollectionName(), LogEntry.class
            );
            while (this.run) {
                LogEntry entry = queue.take();
                collection.insertOne(entry);
            }
        } catch (InterruptedException ignored) {
        }
    }

}
