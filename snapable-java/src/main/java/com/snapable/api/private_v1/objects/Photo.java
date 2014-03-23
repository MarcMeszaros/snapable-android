package com.snapable.api.private_v1.objects;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;
import com.snapable.api.BaseObject;

import java.util.Date;

public class Photo extends BaseObject {
    public String author_name;
    public String caption;
    public Date created_at;
    @SerializedName("event")
    public String event_uri;
    public Boolean streamable;
    @Deprecated public Date timestamp;

    public String toString() {
        return Objects.toStringHelper(this)
            .add("pk", this.getPk())
        	.add("author_name", this.author_name)
        	.add("caption", this.caption)
            .add("created_at", this.created_at)
            .add("event_uri", this.event_uri)
            .toString();
    }

    // virtual properties
    @Deprecated
    public Long getId() {
    	return getPk();
    }

    public Long getEventId() {
        return BaseObject.getPkFromResourceUri(this.event_uri);
    }

}

