package com.snapable.api.private_v1.objects;

import com.google.gson.annotations.SerializedName;
import com.snapable.api.ToStringBuilder;

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
        return new ToStringBuilder(this)
        	.append("enable", this.enabled)
        	.append("end", this.end)
        	.append("photo_count", this.photo_count)
        	.append("pin", this.pin)
        	.append("is_public", this.is_public)
            .append("resource_uri", this.resource_uri)
            .append("start", start)
            .append("title", this.title)
            .append("url", this.url)
            .toString();
    }

    // virtual properties
    public Long getId() {
    	String[] resourceParts = this.resource_uri.split("/");
    	return Long.valueOf(resourceParts[resourceParts.length-1]);
    }

}
