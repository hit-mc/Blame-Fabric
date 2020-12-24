package com.keuin.blame.data.enums.codec;

import com.keuin.blame.data.WorldPos;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;

import java.util.Optional;

public class WorldPosCodec implements Codec<WorldPos> {

    private final Codec<Document> documentCodec;

    public WorldPosCodec() {
        documentCodec = new DocumentCodec();
    }

    public WorldPosCodec(Codec<Document> documentCodec) {
        this.documentCodec = documentCodec;
    }

    @Override
    public WorldPos decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(reader, decoderContext);
        return new WorldPos(
                document.getString("world"),
                document.getDouble("x"),
                document.getDouble("y"),
                document.getDouble("z")
        );
    }

    @Override
    public void encode(BsonWriter writer, WorldPos value, EncoderContext encoderContext) {
        Document document = new Document();
        Optional.ofNullable(value.getWorld()).ifPresent(world -> document.put("world", world));
        document.put("x", value.getX());
        document.put("y", value.getY());
        document.put("z", value.getZ());
        documentCodec.encode(writer, document, encoderContext);
    }

    @Override
    public Class<WorldPos> getEncoderClass() {
        return WorldPos.class;
    }
}
