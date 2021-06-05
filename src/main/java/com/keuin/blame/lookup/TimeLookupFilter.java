package com.keuin.blame.lookup;

import com.keuin.blame.data.entry.LogEntryNames;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

public class TimeLookupFilter implements AbstractLookupFilter {
    private final long seconds;

    public TimeLookupFilter(long seconds) {
        this.seconds = seconds;
    }

    @Override
    public Bson filter() {
        return Filters.gte(
                LogEntryNames.TIMESTAMP_MILLIS,
                System.currentTimeMillis() - seconds * 1000);
    }
}
