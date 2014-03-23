package com.snapable.api.private_v1.objects;

import com.google.common.base.Objects;
import com.snapable.api.private_v1.BaseObject;

import java.util.Date;

public class Event extends BaseObject {

    public Date end_at;
    public Long photo_count;
    public String pin;
    public Boolean is_enabled;
    public Boolean is_public;
    public Date start_at;
    public String title;
    public String url;
    @Deprecated public Boolean enabled;
    @Deprecated public Date end;
    @Deprecated public Date start;

    public String toString() {
        return Objects.toStringHelper(this)
            .add("pk", this.getPk())
        	.add("end_at", this.end_at)
        	.add("photo_count", this.photo_count)
        	.add("pin", this.pin)
            .add("is_enable", this.is_enabled)
            .add("is_public", this.is_public)
            .add("start_at", start_at)
            .add("title", this.title)
            .add("url", this.url)
            .toString();
    }

    // virtual properties
    @Deprecated
    public Long getId() {
    	return getPk();
    }

}
