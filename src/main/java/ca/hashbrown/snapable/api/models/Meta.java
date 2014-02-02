package ca.hashbrown.snapable.api.models;

import ca.hashbrown.snapable.api.ToStringBuilder;

public class Meta {

    public long limit;
    public String next;
    public long offset;
    public String previous;
    public long total_count;

    public String toString() {
        return new ToStringBuilder(this)
                .append("limit", limit)
                .append("offset", offset)
                .append("total_count", total_count)
                .toString();
    }
}

