package com.snapable.api.private_v1.objects;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Event {
    public Date end_at;
    public Long photo_count;
    public String pin;
    public Boolean is_enabled;
    public Boolean is_public;
    public String resource_uri;
    public Date start_at;
    public String title;
    public String url;
    @Deprecated public Boolean enabled;
    @Deprecated public Date end;
    @Deprecated public Date start;

    public String toString() {
        return Objects.toStringHelper(this)
        	.add("end_at", this.end_at)
        	.add("photo_count", this.photo_count)
        	.add("pin", this.pin)
            .add("is_enable", this.is_enabled)
            .add("is_public", this.is_public)
            .add("resource_uri", this.resource_uri)
            .add("start_at", start_at)
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
