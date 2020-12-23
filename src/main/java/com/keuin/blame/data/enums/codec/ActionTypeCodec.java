package com.keuin.blame.data.enums.codec;

import com.keuin.blame.data.enums.ActionType;
import org.bson.BsonReader;
import org.bson.codecs.DecoderContext;

public class ActionTypeCodec extends AbstractIntegerEnumCodec<ActionType> {
    @Override
    public ActionType decode(BsonReader reader, DecoderContext decoderContext) {
        return ActionType.parseInt(reader.readInt32());
    }

    @Override
    public Class<ActionType> getEncoderClass() {
        return ActionType.class;
    }
}
