package com.keuin.blame.data.transformer;

import com.keuin.blame.data.WorldPos;
import com.keuin.blame.data.entry.LogEntry;

public class LogEntryV1ToV2Transformer extends AbstractLogEntryTransformer {

    private static final LogEntryV1ToV2Transformer INSTANCE = new LogEntryV1ToV2Transformer();

    static {
        TransformerManager.setTransformer(1, INSTANCE);
    }

    @Override
    public LogEntry transform(LogEntry entry) {
        LogEntry entryV2 = new LogEntry();
        entryV2.version = 2;
        entryV2.gameVersion = entry.gameVersion;
        entryV2.timeMillis = entry.timeMillis;
        entryV2.subjectId = entry.subjectId;
        entryV2.subjectUUID = entry.subjectUUID;
        entryV2.subjectPos = entry.subjectPos;
        entryV2.actionType = entry.actionType;
        entryV2.objectType = entry.objectType;
        entryV2.objectId = entry.objectId;
        entryV2.objectPos = entry.objectPos;
        entryV2.radius = getRadius(entry.objectPos);
        return entryV2;
    }

    private static double getRadius(WorldPos objectPos) {
        return Math.sqrt(Math.pow(objectPos.getX(), 2) + Math.pow(objectPos.getY(), 2) + Math.pow(objectPos.getZ(), 2));
    }
}
