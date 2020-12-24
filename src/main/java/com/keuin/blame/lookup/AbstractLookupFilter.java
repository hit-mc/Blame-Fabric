package com.keuin.blame.lookup;

import com.keuin.blame.data.LogEntry;
import com.mongodb.client.FindIterable;

public abstract class AbstractLookupFilter {
    // immutable

    AbstractLookupFilter() {
    }

    abstract FindIterable<LogEntry> find(FindIterable<LogEntry> iterable);
}