package com.keuin.blame.lookup;

import com.keuin.blame.data.WorldPos;
import com.keuin.blame.data.entry.LogEntry;
import com.keuin.blame.data.entry.LogEntryNames;
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
                Filters.eq(LogEntryNames.VERSION, 1),
                Filters.eq(LogEntryNames.OBJECT_POS, blockPos)
        ));
    }
}
