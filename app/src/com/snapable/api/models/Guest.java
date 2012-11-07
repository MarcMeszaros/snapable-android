package com.snapable.api.models;

import org.codegist.common.lang.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

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
	
    public String toString() {
        return new ToStringBuilder(this)
            .append("email", this.email)
            .append("eventUri", this.eventUri)
        	.append("name", this.name)
        	.append("resourceUri", this.resourceUri)
        	.append("typeUri", this.typeUri)
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
