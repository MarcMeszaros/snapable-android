package com.snapable.api.models;

import java.util.Date;

import org.codegist.common.lang.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import android.provider.BaseColumns;

public class Photo {
	@JsonProperty("author_name")
    private String authorName;
    @JsonProperty("caption")
    private String caption;
    @JsonProperty("event")
    private String eventUri;
    @JsonProperty("resource_uri")
    private String resourceUri;
    @JsonProperty("streamable")
    private boolean isStreamable;
    @JsonProperty("timestamp")
    private Date timestamp;
    @JsonProperty("type")
    private String typeUri;

    // fields
    public static final String FIELD_ID = BaseColumns._ID;
    public static final String FIELD_AUTHOR_NAME = "authorName";
    public static final String FIELD_CAPTION = "caption";
    public static final String FIELD_EVENT_URI = "eventUri";
    public static final String FIELD_RESOURCE_URI = "resourceUri";
    public static final String FIELD_STREAMABLE = "streamable";
    public static final String FIELD_TIMESTAMP = "timestamp";
    public static final String FIELD_TYPE_URI = "typeUri";
    
    // required for a Cursor implementation
    public static final String[] COLUMN_NAMES = {
    	FIELD_ID,
    	FIELD_AUTHOR_NAME,
    	FIELD_CAPTION,
    	FIELD_EVENT_URI,
    	FIELD_RESOURCE_URI,
    	FIELD_STREAMABLE,
    	FIELD_TIMESTAMP,
    	FIELD_TYPE_URI
    };

    public String toString() {
        return new ToStringBuilder(this)
        	.append("authorName", this.authorName)
        	.append("caption", this.caption)
            .append("eventUri", this.eventUri)
            .append("resourceUri", this.resourceUri)
            .append("timestamp", this.timestamp)
            .append("typeUri", this.typeUri)
            .toString();
    }

    // properties
    public String getAuthorName() {
        return this.authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    
    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getEventUri() {
        return this.eventUri;
    }

    public void setEventUri(String eventUri) {
        this.eventUri = eventUri;
    }
    
    public String getResourceUri() {
        return this.resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }
    
    public boolean getIsStreamable() {
        return this.isStreamable;
    }

    public void setIsStreamable(boolean isStreamable) {
        this.isStreamable = isStreamable;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getTypeUri() {
        return this.typeUri;
    }

    public void setTypeUri(String typeUri) {
        this.typeUri = typeUri;
    }

    // virtual properties
    public long getId() {
    	String[] resourceParts = this.resourceUri.split("/");
    	return Long.valueOf(resourceParts[resourceParts.length-1]);
    }
    
    public long getEventId() {
    	String[] resourceParts = this.eventUri.split("/");
    	return Long.valueOf(resourceParts[resourceParts.length-1]);
    }
    
    public long getTypeId() {
    	String[] resourceParts = this.typeUri.split("/");
    	return Long.valueOf(resourceParts[resourceParts.length-1]);
    }

}

