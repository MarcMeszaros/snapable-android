package com.snapable.api.models;

import com.snapable.api.ToStringBuilder;

import java.util.List;

public class Pager<E> {

    public Meta meta;
    public List<E> objects;

    public String toString() {
        return new ToStringBuilder(this)
                .append("meta", meta)
                .append("objects", objects)
                .toString();
    }
}
