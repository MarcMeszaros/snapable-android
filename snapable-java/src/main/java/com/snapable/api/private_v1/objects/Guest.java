package com.snapable.api.private_v1.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.snapable.utils.ToStringHelper;

public class Guest extends BaseObject {
    public String email;

    @JsonProperty("event")
    public String event_uri;
    public String name;

	public String toString() {
        return ToStringHelper.getInstance(this)
            .add("pk", this.getPk())
            .add("email", this.email)
            .add("event_uri", this.event_uri)
        	.add("name", this.name)
            .toString();
    }

    // virtual properties
    @JsonIgnore
    @Deprecated
    public Long getId() {
    	return getPk();
    }

    @JsonIgnore
    public void setEvent(long eventId) {
        event_uri = new Event().getResourceUriFromLong(eventId);
    }

    @JsonIgnore
    public Long getEventId() {
        return BaseObject.getPkFromResourceUri(this.event_uri);
    }
}
