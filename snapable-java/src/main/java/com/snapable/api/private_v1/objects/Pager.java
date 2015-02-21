package com.snapable.api.private_v1.objects;

import com.snapable.utils.ToStringHelper;

import java.util.ArrayList;

public class Pager<E> {

    public Meta meta;
    public ArrayList<E> objects;

    public String toString() {
        return ToStringHelper.getInstance(this)
                .add("meta", meta)
                .add("objects", objects)
                .toString();
    }

    public class Meta {

        public long limit;
        public String next;
        public long offset;
        public String previous;
        public long totalCount;

        public String toString() {
            return ToStringHelper.getInstance(this)
                    .add("limit", limit)
                    .add("offset", offset)
                    .add("total_count", totalCount)
                    .toString();
        }

    }
}
