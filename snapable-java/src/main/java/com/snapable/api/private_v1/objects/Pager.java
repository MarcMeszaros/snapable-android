package com.snapable.api.private_v1.objects;

import com.google.common.base.Objects;

import java.util.List;

public class Pager<E> {

    public Meta meta;
    public List<E> objects;

    public String toString() {
        return Objects.toStringHelper(this)
                .add("meta", meta)
                .add("objects", objects)
                .toString();
    }
}
