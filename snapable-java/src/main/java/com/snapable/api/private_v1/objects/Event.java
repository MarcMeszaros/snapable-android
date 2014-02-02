package com.snapable.api.private_v1.objects;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Event {
    public Boolean enabled;
    public Date end;
    public Long photo_count;
    public String pin;
    @SerializedName("public")
    public Boolean is_public;
    public String resource_uri;
    public Date start;
    public String title;
    public String url;

    public String toString() {
        return Objects.toStringHelper(this)
        	.add("enable", this.enabled)
        	.add("end", this.end)
        	.add("photo_count", this.photo_count)
        	.add("pin", this.pin)
        	.add("is_public", this.is_public)
            .add("resource_uri", this.resource_uri)
            .add("start", start)
            .add("title", this.title)
            .add("url", this.url)
            .toString();
    }

    // virtual properties
    public Long getId() {
    	String[] resourceParts = this.resource_uri.split("/");
    	return Long.valueOf(resourceParts[resourceParts.length-1]);
    }

}
