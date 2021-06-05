package com.keuin.blame.lookup;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;

public class DummyFilter implements AbstractLookupFilter {

    @Override
    public Bson filter() {
        return new BsonDocument();
    }
}
