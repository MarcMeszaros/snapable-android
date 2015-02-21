package com.snapable.api.private_v1.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.snapable.api.private_v1.Client;

abstract class BaseObject extends com.snapable.api.BaseObject {

    @JsonIgnore
    @Override
    public String getResourceUriFromLong(Long pk) {
        return String.format("/%s/%s/%d/", Client.VERSION, getClass().getSimpleName().toLowerCase(), pk);
    }

}
