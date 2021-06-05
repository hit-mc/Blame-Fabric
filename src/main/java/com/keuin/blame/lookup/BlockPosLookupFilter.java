package com.keuin.blame.lookup;

import com.keuin.blame.data.WorldPos;
import com.keuin.blame.data.entry.LogEntryNames;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

public class BlockPosLookupFilter implements AbstractLookupFilter {
    private final WorldPos blockPos;

    public BlockPosLookupFilter(WorldPos blockPos) {
        this.blockPos = blockPos;
    }

    @Override
    public Bson filter() {
        return Filters.and(
                Filters.eq(LogEntryNames.VERSION, 1),
                Filters.eq(LogEntryNames.OBJECT_POS, blockPos)
        );
    }
}
