package com.snapable.api.private_v1.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.snapable.utils.ToStringHelper;

import java.util.Date;

public class Photo extends BaseObject {
    public String author_name;
    public String caption;
    public Date created_at;
    @JsonProperty("event")
    public String event_uri;
    public Boolean streamable;
    @Deprecated public Date timestamp;

    public String toString() {
        return ToStringHelper.getInstance(this)
            .add("pk", this.getPk())
        	.add("author_name", this.author_name)
        	.add("caption", this.caption)
            .add("created_at", this.created_at)
            .add("event_uri", this.event_uri)
            .toString();
    }

    // virtual properties
    @JsonIgnore
    @Deprecated
    public Long getId() {
    	return getPk();
    }

    @JsonIgnore
    public Long getEventId() {
        return BaseObject.getPkFromResourceUri(this.event_uri);
    }

}

