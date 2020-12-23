package com.keuin.blame.data.enums.codec;

import com.keuin.blame.data.enums.ObjectType;
import org.bson.BsonReader;
import org.bson.codecs.DecoderContext;

public class ObjectTypeCodec extends AbstractIntegerEnumCodec<ObjectType> {
    @Override
    public ObjectType decode(BsonReader reader, DecoderContext decoderContext) {
        return ObjectType.parseInt(reader.readInt32());
    }

    @Override
    public Class<ObjectType> getEncoderClass() {
        return ObjectType.class;
    }
}
