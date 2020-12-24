package com.keuin.blame.lookup;

import com.keuin.blame.data.LogEntry;
import com.keuin.blame.data.enums.ActionType;
import com.keuin.blame.data.enums.codec.LogEntryNames;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;

public class TestableFilter extends AbstractLookupFilter {
    @Override
    protected FindIterable<LogEntry> find(FindIterable<LogEntry> iterable) {
        return iterable.filter(Filters.eq(LogEntryNames.ACTION_TYPE, ActionType.NULL.getValue()));
    }
}
