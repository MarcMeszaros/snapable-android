package com.snapable.api.private_v1.objects;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Photo {
    public String author_name;
    public String caption;
    @SerializedName("event")
    public String event_uri;
    public String resource_uri;
    public Boolean streamable;
    public Date timestamp;

    public String toString() {
        return Objects.toStringHelper(this)
        	.add("author_name", this.author_name)
        	.add("caption", this.caption)
            .add("event_uri", this.event_uri)
            .add("resource_uri", this.resource_uri)
            .add("timestamp", this.timestamp)
            .toString();
    }

    // virtual properties
    public Long getId() {
    	String[] resourceParts = this.resource_uri.split("/");
    	return Long.valueOf(resourceParts[resourceParts.length-1]);
    }

    public Long getEventId() {
    	String[] resourceParts = this.event_uri.split("/");
    	return Long.valueOf(resourceParts[resourceParts.length-1]);
    }

}

