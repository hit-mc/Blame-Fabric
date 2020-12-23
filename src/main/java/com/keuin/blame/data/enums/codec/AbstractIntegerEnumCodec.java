package com.keuin.blame.data.enums.codec;

import com.keuin.blame.data.enums.IntegerEnum;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;

public abstract class AbstractIntegerEnumCodec<T extends IntegerEnum> implements Codec<T> {
    @Override
    public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
        writer.writeInt32(value.getValue());
    }
}
