package com.snapable.api.private_v1.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.snapable.utils.ToStringHelper;

public class Guest extends BaseObject {

    public String email;
    @JsonProperty("event")
    public String eventUri;
    public String name;

	public String toString() {
        return ToStringHelper.getInstance(this)
            .add("pk", this.getPk())
            .add("email", this.email)
            .add("eventUri", this.eventUri)
        	.add("name", this.name)
            .toString();
    }

    // virtual properties
    @JsonIgnore
    public void setEvent(long eventId) {
        eventUri = new Event().getResourceUriFromLong(eventId);
    }

    @JsonIgnore
    public Long getEventId() {
        return BaseObject.getPkFromResourceUri(this.eventUri);
    }
}
