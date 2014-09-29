package com.snapable.api.private_v1.objects;

import com.snapable.utils.ToStringHelper;

import java.util.List;

public class Pager<E> {

    public Meta meta;
    public List<E> objects;

    public String toString() {
        return ToStringHelper.getInstance(this)
                .add("meta", meta)
                .add("objects", objects)
                .toString();
    }
}
