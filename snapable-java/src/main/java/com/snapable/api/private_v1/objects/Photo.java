package com.snapable.api.private_v1.objects;

import com.google.gson.annotations.SerializedName;
import com.snapable.api.ToStringBuilder;

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
        return new ToStringBuilder(this)
        	.append("author_name", this.author_name)
        	.append("caption", this.caption)
            .append("event_uri", this.event_uri)
            .append("resource_uri", this.resource_uri)
            .append("timestamp", this.timestamp)
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

