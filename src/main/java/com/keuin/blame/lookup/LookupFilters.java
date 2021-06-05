package com.keuin.blame.lookup;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class LookupFilters {
    public static AbstractLookupFilter compoundedFilter(AbstractLookupFilter... filters) {
        return () -> {
            List<Bson> list = new ArrayList<>();
            for (AbstractLookupFilter filter : filters) {
                Bson bson = filter.filter();
                list.add(bson);
            }
            return Filters.and(list);
        };
    }
}
