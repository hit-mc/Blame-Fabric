package com.keuin.blame.data.enums.transformer;

import com.keuin.blame.data.enums.ActionType;
import org.bson.Transformer;

public class ActionTypeTransformer implements Transformer {
    @Override
    public Object transform(Object objectToTransform) {
        ActionType actionType = (ActionType) objectToTransform;
        return actionType.getValue();
    }
}
