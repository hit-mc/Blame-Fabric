package com.keuin.blame.lookup;

import org.bson.conversions.Bson;

public interface AbstractLookupFilter {
    abstract Bson filter();
}