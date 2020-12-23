package com.keuin.blame.data.enums.transformer;

import com.keuin.blame.data.enums.ObjectType;
import org.bson.Transformer;

public class ObjectTypeTransformer implements Transformer {
    @Override
    public Object transform(Object objectToTransform) {
        ObjectType objectType = (ObjectType) objectToTransform;
        return objectType.getValue();
    }
}
