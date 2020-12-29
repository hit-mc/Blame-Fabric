package com.keuin.blame.data.transformer;

import com.keuin.blame.data.entry.LogEntry;
import com.keuin.blame.util.MinecraftUtil;

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
        entryV2.radius = MinecraftUtil.getRadius(entry.objectPos);
        return entryV2;
    }

}
