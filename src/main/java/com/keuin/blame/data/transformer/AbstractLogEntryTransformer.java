package com.keuin.blame.data.transformer;

import com.keuin.blame.data.entry.LogEntry;

public abstract class AbstractLogEntryTransformer {
    public abstract LogEntry transform(LogEntry entry);
}
