package com.keuin.blame.lookup;

import com.keuin.blame.data.LogEntry;

public interface LookupCallback {
    void onLookupFinishes(Iterable<LogEntry> logEntries);
}
