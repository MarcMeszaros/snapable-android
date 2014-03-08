package com.snapable.api.private_v1.objects;

import com.google.common.base.Objects;

public class Meta {

    public long limit;
    public String next;
    public long offset;
    public String previous;
    public long total_count;

    public String toString() {
        return Objects.toStringHelper(this)
                .add("limit", limit)
                .add("offset", offset)
                .add("total_count", total_count)
                .toString();
    }
}

