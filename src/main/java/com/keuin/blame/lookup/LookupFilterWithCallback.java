package com.keuin.blame.lookup;

import java.util.Objects;

class LookupFilterWithCallback {

    private final LookupCallback callback;
    private final AbstractLookupFilter filter;
    private final int limit;

    LookupFilterWithCallback(LookupCallback callback, AbstractLookupFilter filter, int limit) {
        if (callback == null)
            throw new IllegalArgumentException("callback cannot be null");
        if (filter == null)
            throw new IllegalArgumentException("filter cannot be null");
        if (limit <= 0)
            throw new IllegalArgumentException("limit must be positive");
        this.callback = callback;
        this.filter = filter;
        this.limit = limit;
    }

    public LookupCallback getCallback() {
        return callback;
    }

    public AbstractLookupFilter getFilter() {
        return filter;
    }

    public int getLimit() {
        return limit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LookupFilterWithCallback that = (LookupFilterWithCallback) o;
        return limit == that.limit &&
                callback.equals(that.callback) &&
                filter.equals(that.filter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(callback, filter, limit);
    }
}
