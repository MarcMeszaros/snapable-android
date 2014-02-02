package ca.hashbrown.snapable.api.models;

import android.provider.BaseColumns;
import com.google.gson.annotations.SerializedName;
import ca.hashbrown.snapable.api.ToStringBuilder;

public class Guest {
    public String email;
    @SerializedName("event")
    public String event_uri;
    public String name;
    public String resource_uri;

	// fields
    public static final String FIELD_ID = BaseColumns._ID;
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_EVENT_URI = "event_uri";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_RESOURCE_URI = "resource_uri";

    // required for a Cursor implementation
    public static final String[] COLUMN_NAMES = {
    	FIELD_ID,
    	FIELD_EMAIL,
    	FIELD_EVENT_URI,
    	FIELD_NAME,
    	FIELD_RESOURCE_URI
    };

    public String toString() {
        return new ToStringBuilder(this)
            .append(FIELD_EMAIL, this.email)
            .append(FIELD_EVENT_URI, this.event_uri)
        	.append(FIELD_NAME, this.name)
        	.append(FIELD_RESOURCE_URI, this.resource_uri)
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
