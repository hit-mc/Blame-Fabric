package com.keuin.blame.lookup;

import com.keuin.blame.data.WorldPos;
import com.keuin.blame.data.entry.LogEntry;
import com.keuin.blame.data.entry.LogEntryNamesV1;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;

public class BlockPosLookupFilter extends AbstractLookupFilter {
    private final WorldPos blockPos;

    public BlockPosLookupFilter(WorldPos blockPos) {
        this.blockPos = blockPos;
    }

    @Override
    FindIterable<LogEntry> find(FindIterable<LogEntry> iterable) {
        return iterable.filter(Filters.and(
                Filters.eq(LogEntryNamesV1.VERSION, 1),
                Filters.eq(LogEntryNamesV1.OBJECT_POS, blockPos)
        ));
    }
}
