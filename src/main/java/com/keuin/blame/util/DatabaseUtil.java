package com.keuin.blame.util;

import com.keuin.blame.Blame;
import com.keuin.blame.config.MongoConfig;
import com.keuin.blame.data.enums.codec.ActionTypeCodec;
import com.keuin.blame.data.enums.codec.ObjectTypeCodec;
import com.keuin.blame.data.enums.codec.WorldPosCodec;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

public class DatabaseUtil {

//    static {
//        BSON.addEncodingHook(ActionType.class, new ActionTypeTransformer());
//        BSON.addEncodingHook(ObjectType.class, new ObjectTypeTransformer());
//    }

    public static final MongoConfig MONGO_CONFIG = Blame.config.getMongoConfig();
    public static final CodecRegistry CODEC_REGISTRY = CodecRegistries.fromRegistries(
            com.mongodb.MongoClient.getDefaultCodecRegistry(),
            CodecRegistries.fromCodecs(
                    new ActionTypeCodec(),
                    new ObjectTypeCodec(),
                    new WorldPosCodec()
//                    new LogEntryCodec()
            ),
            fromProviders(PojoCodecProvider.builder().automatic(true).build())
    );
    public static final MongoClientSettings CLIENT_SETTINGS = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(MONGO_CONFIG.getAddress()))
            .codecRegistry(CODEC_REGISTRY)
            .build();
}