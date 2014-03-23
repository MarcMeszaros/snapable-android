package com.snapable.api.private_v1.objects;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;
import com.snapable.api.BaseObject;

public class Guest extends BaseObject {
    public String email;
    @SerializedName("event")
    public String event_uri;
    public String name;

	public String toString() {
        return Objects.toStringHelper(this)
            .add("pk", this.getPk())
            .add("email", this.email)
            .add("event_uri", this.event_uri)
        	.add("name", this.name)
            .toString();
    }

    // virtual properties
    @Deprecated
    public Long getId() {
    	return getPk();
    }

    public Long getEventId() {
        return BaseObject.getPkFromResourceUri(this.event_uri);
    }
}
