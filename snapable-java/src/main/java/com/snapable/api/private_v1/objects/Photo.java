package com.snapable.api.private_v1.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.snapable.utils.ToStringHelper;

import java.util.Date;

public class Photo extends BaseObject {

    public String authorName;
    public String caption;
    public Date createdAt;
    @JsonProperty("event")
    public String eventUri;
    public Boolean isStreamable;

    public String toString() {
        return ToStringHelper.getInstance(this)
            .add("pk", this.getPk())
        	.add("author_name", this.authorName)
        	.add("caption", this.caption)
            .add("created_at", this.createdAt)
            .add("event_uri", this.eventUri)
            .add("is_streamable", this.isStreamable)
            .toString();
    }

    // virtual properties
    @JsonIgnore
    public Long getEventId() {
        return BaseObject.getPkFromResourceUri(this.eventUri);
    }

}

