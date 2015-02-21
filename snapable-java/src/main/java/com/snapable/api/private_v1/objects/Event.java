package com.snapable.api.private_v1.objects;

import com.snapable.utils.ToStringHelper;

import java.util.Date;

public class Event extends BaseObject {

    public Date endAt;
    public Long photoCount;
    public String pin;
    public Boolean isEnabled;
    public Boolean isPublic;
    public Date startAt;
    public String title;
    public String url;

    public String toString() {
        return ToStringHelper.getInstance(this)
            .add("pk", this.getPk())
        	.add("end_at", this.endAt)
        	.add("photo_count", this.photoCount)
        	.add("pin", this.pin)
            .add("is_enabled", this.isEnabled)
            .add("is_public", this.isPublic)
            .add("startAt", startAt)
            .add("title", this.title)
            .add("url", this.url)
            .toString();
    }

}
