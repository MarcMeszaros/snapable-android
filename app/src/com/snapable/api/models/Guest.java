package com.snapable.api.models;

import org.codegist.common.lang.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import android.provider.BaseColumns;

public class Guest {
	@JsonProperty("email")
	private String email;
	@JsonProperty("event")
	private String eventUri;
	@JsonProperty("name")
	private String name;
	@JsonProperty("resource_uri")
	private String resourceUri;
	@JsonProperty("type")
	private String typeUri;
	
	// fields
    public static final String FIELD_ID = BaseColumns._ID;
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_EVENT_URI = "eventUri";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_RESOURCE_URI = "resourceUri";
    public static final String FIELD_TYPE_URI = "typeUri";
    
    // required for a Cursor implementation
    public static final String[] COLUMN_NAMES = {
    	FIELD_ID,
    	FIELD_EMAIL,
    	FIELD_EVENT_URI,
    	FIELD_NAME,
    	FIELD_RESOURCE_URI,
    	FIELD_TYPE_URI
    };
	
    public String toString() {
        return new ToStringBuilder(this)
            .append(FIELD_EMAIL, this.email)
            .append(FIELD_EVENT_URI, this.eventUri)
        	.append(FIELD_NAME, this.name)
        	.append(FIELD_RESOURCE_URI, this.resourceUri)
        	.append(FIELD_TYPE_URI, this.typeUri)
            .toString();
    }

    // properties
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getEventUri() {
        return this.eventUri;
    }

    public void setEventUri(String eventUri) {
        this.eventUri = eventUri;
    }
    
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getResourceUri() {
        return this.resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
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
