package com.keuin.blame.lookup;

import java.util.Objects;

class LookupFilterWithCallback {

    private final LookupCallback callback;
    private final AbstractLookupFilter filter;

    LookupFilterWithCallback(LookupCallback callback, AbstractLookupFilter filter) {
        if (callback == null)
            throw new IllegalArgumentException("callback cannot be null");
        if (filter == null)
            throw new IllegalArgumentException("filter cannot be null");
        this.callback = callback;
        this.filter = filter;
    }

    public LookupCallback getCallback() {
        return callback;
    }

    public AbstractLookupFilter getFilter() {
        return filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LookupFilterWithCallback that = (LookupFilterWithCallback) o;
        return callback.equals(that.callback) &&
                filter.equals(that.filter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(callback, filter);
    }

}
