package com.keuin.blame.lookup;

import com.keuin.blame.data.entry.LogEntry;
import com.mongodb.client.FindIterable;

public class DummyFilter extends AbstractLookupFilter {
    @Override
    protected FindIterable<LogEntry> find(FindIterable<LogEntry> iterable) {
        return iterable;
    }
}
