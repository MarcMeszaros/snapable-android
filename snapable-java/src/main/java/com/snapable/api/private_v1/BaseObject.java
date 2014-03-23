package com.snapable.api.private_v1;

public abstract class BaseObject extends com.snapable.api.BaseObject {

    @Override
    public String getResourceUriFromLong(Long pk) {
        return String.format("/%s/%s/%d/", Client.VERSION, getClass().getSimpleName().toLowerCase(), pk);
    }

}
