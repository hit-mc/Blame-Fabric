package com.keuin.blame.data.enums.codec;

import com.keuin.blame.data.LogEntry;
import com.keuin.blame.data.WorldPos;
import com.keuin.blame.data.enums.ActionType;
import com.keuin.blame.data.enums.ObjectType;
import org.bson.*;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.Objects;
import java.util.UUID;

import static com.keuin.blame.data.enums.codec.LogEntryNames.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

public class LogEntryCodec implements CollectibleCodec<LogEntry> {

    private final Codec<Document> documentCodec;

    public LogEntryCodec() {
        CodecRegistry CODEC_REGISTRY = CodecRegistries.fromRegistries(
                com.mongodb.MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs(
                        new ActionTypeCodec(),
                        new ObjectTypeCodec(),
                        new WorldPosCodec()
                ),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );
        documentCodec = new DocumentCodec(
                CODEC_REGISTRY
        );
    }

    public LogEntryCodec(Codec<Document> documentCodec) {
        this.documentCodec = documentCodec;
    }


    @Override
    public LogEntry decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(reader, decoderContext);
        Integer entryVersion = document.getInteger("version");
        if (entryVersion == null)
            return LogEntry.EMPTY_ENTRY;
        if (Objects.equals(LogEntry.EMPTY_ENTRY.getVersion(), entryVersion)) {
            LogEntry entry = new LogEntry(
                    document.getLong(TIMESTAMP_MILLIS),
                    document.getString(SUBJECT_ID),
                    document.get(SUBJECT_UUID, UUID.class),
                    WorldPos.NULL_POS,
//                    document.get(SUBJECT_POS, WorldPos.class),
                    ActionType.parseInt(document.getInteger(ACTION_TYPE)),
                    ObjectType.parseInt(document.getInteger(OBJECT_TYPE)),
                    document.getString(OBJECT_ID),
                    WorldPos.NULL_POS
//                    document.get(OBJECT_POS, WorldPos.class)
            );
            return entry;
        }
        throw new RuntimeException(String.format("unsupported LogEntry version: %d. Perhaps your Blame is too old.", entryVersion));
    }

    @Override
    public void encode(BsonWriter writer, LogEntry value, EncoderContext encoderContext) {
        Document document = new Document();

        document.put(VERSION, value.getVersion());
        document.put(GAME_VERSION, value.getGameVersion());
        document.put(TIMESTAMP_MILLIS, value.getTimeMillis());
        document.put(SUBJECT_ID, value.getSubjectId());
        document.put(SUBJECT_UUID, value.getSubjectUUID());
//        document.put(SUBJECT_POS, value.getSubjectPos());
        document.put(ACTION_TYPE, value.getActionType().getValue());
        document.put(OBJECT_TYPE, value.getObjectType().getValue());
        document.put(OBJECT_ID, value.getObjectId());
//        document.put(OBJECT_POS, value.getObjectPos());

        documentCodec.encode(writer, document, encoderContext);
    }

    @Override
    public Class<LogEntry> getEncoderClass() {
        return LogEntry.class;
    }

    @Override
    public LogEntry generateIdIfAbsentFromDocument(LogEntry document) {
        return document;
    }

    @Override
    public boolean documentHasId(LogEntry document) {
        return document.getObjectId() != null;
    }

    @Override
    public BsonValue getDocumentId(LogEntry document) {
        return new BsonString(document.getObjectId());
    }
}
