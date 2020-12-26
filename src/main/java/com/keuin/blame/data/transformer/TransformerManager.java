package com.keuin.blame.data.transformer;

import com.keuin.blame.data.entry.LogEntry;

import java.util.HashMap;
import java.util.Map;

public class TransformerManager {

    public static int LATEST_VERSION = 2;

    private static final Map<Integer, AbstractLogEntryTransformer> transformerMap = new HashMap<>();

    static void setTransformer(int baseVersion, AbstractLogEntryTransformer transformer) {
        transformerMap.put(baseVersion, transformer);
    }

    public static Object toLatestVersion(LogEntry baseEntry) {
        return transformTo(LATEST_VERSION, baseEntry);
    }

    public static Object transformTo(int targetVersion, LogEntry baseEntry) {
        LogEntry entry = baseEntry;
        for (int currentVersion = baseEntry.version; currentVersion != targetVersion; ++currentVersion)
            entry = transformerMap.get(currentVersion + 1).transform(entry);
        return entry;
    }
}
