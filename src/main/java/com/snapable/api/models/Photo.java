package com.snapable.api.models;

import android.provider.BaseColumns;
import com.google.gson.annotations.SerializedName;
import com.snapable.api.ToStringBuilder;

import java.util.Date;

public class Photo {
    public String author_name;
    public String caption;
    @SerializedName("event")
    public String event_uri;
    public String resource_uri;
    public Boolean streamable;
    public Date timestamp;

    // fields
    public static final String FIELD_ID = BaseColumns._ID;
    public static final String FIELD_AUTHOR_NAME = "author_name";
    public static final String FIELD_CAPTION = "caption";
    public static final String FIELD_EVENT_URI = "event_uri";
    public static final String FIELD_RESOURCE_URI = "resource_uri";
    public static final String FIELD_STREAMABLE = "streamable";
    public static final String FIELD_TIMESTAMP = "timestamp";

    // required for a Cursor implementation
    public static final String[] COLUMN_NAMES = {
    	FIELD_ID,
    	FIELD_AUTHOR_NAME,
    	FIELD_CAPTION,
    	FIELD_EVENT_URI,
    	FIELD_RESOURCE_URI,
    	FIELD_STREAMABLE,
    	FIELD_TIMESTAMP
    };

    public String toString() {
        return new ToStringBuilder(this)
        	.append(FIELD_AUTHOR_NAME, this.author_name)
        	.append(FIELD_CAPTION, this.caption)
            .append(FIELD_EVENT_URI, this.event_uri)
            .append(FIELD_RESOURCE_URI, this.resource_uri)
            .append(FIELD_TIMESTAMP, this.timestamp)
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

