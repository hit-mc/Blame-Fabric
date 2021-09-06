package com.keuin.blame.util;

import com.keuin.blame.Blame;
import com.keuin.blame.config.MongoConfig;
import com.keuin.blame.data.enums.codec.ActionTypeCodec;
import com.keuin.blame.data.enums.codec.ObjectTypeCodec;
import com.keuin.blame.data.enums.codec.WorldPosCodec;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

public class DatabaseUtil {

    public static final MongoConfig MONGO_CONFIG = Blame.config.getMongoConfig();
    public static final CodecRegistry CODEC_REGISTRY = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromCodecs(
                    new ActionTypeCodec(),
                    new ObjectTypeCodec(),
                    new WorldPosCodec()
            ),
            fromProviders(PojoCodecProvider.builder().automatic(true).build())
    );
    public static final MongoClientSettings CLIENT_SETTINGS = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(MONGO_CONFIG.getAddress()))
            .codecRegistry(CODEC_REGISTRY)
            .uuidRepresentation(UuidRepresentation.JAVA_LEGACY) // for backward-compatible with logs created by older versions
            .build();

    // TODO: Auto create indexes if the collection is empty
    //   db.log.createIndex({ timestamp_millis: -1 })
    //   db.log.createIndex({ timestamp_millis: -1, object_id: "hashed" })
    //   db.log.createIndex({ timestamp_millis: -1, subject_id: "hashed" })

    public static void disableMongoSpamming() {
//        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);
        String[] mongoDrivers = new String[]{"org.mongodb", "org.mongodb.driver", "org.mongodb.driver.cluster", "org.mongodb.driver.connection"};
        for (String driverName : mongoDrivers) {
            Logger mongoLogger = Logger.getLogger(driverName);
            mongoLogger.setLevel(Level.OFF); // Plz be silent, my boy.
        }
    }
}
