package com.snapable.api.private_v1.objects;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

public class Guest {
    public String email;
    @SerializedName("event")
    public String event_uri;
    public String name;
    public String resource_uri;

	public String toString() {
        return Objects.toStringHelper(this)
            .add("email", this.email)
            .add("event_uri", this.event_uri)
        	.add("name", this.name)
        	.add("resource_uri", this.resource_uri)
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
