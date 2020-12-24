package com.keuin.blame.lookup;

import com.keuin.blame.data.entry.LogEntry;

public interface LookupCallback {
    void onLookupFinishes(Iterable<LogEntry> logEntries);
}
