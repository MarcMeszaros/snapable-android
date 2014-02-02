package com.snapable.api.private_v1.objects;

import com.google.gson.annotations.SerializedName;
import com.snapable.api.ToStringBuilder;

public class Guest {
    public String email;
    @SerializedName("event")
    public String event_uri;
    public String name;
    public String resource_uri;

	public String toString() {
        return new ToStringBuilder(this)
            .append("email", this.email)
            .append("event_uri", this.event_uri)
        	.append("name", this.name)
        	.append("resource_uri", this.resource_uri)
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
