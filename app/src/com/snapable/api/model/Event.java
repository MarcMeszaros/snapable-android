package com.snapable.api.model;

import org.codegist.common.lang.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

public class Event {
	@JsonProperty("photo_count")
    private long photoCount;
    @JsonProperty("title")
    private String title;
    @JsonProperty("resource_uri")
    private String resourceUri;
    
    public String toString() {
        return new ToStringBuilder(this)
                .append("title", this.title)
                .append("resourceUri", resourceUri)
                .toString();
    }

    public long getPhotoCount() {
        return this.photoCount;
    }

    public void setPhotoCount(long photoCount) {
        this.photoCount = photoCount;
    }
    
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getResourceUri() {
        return this.resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }
    
    // virtual properties
    public long getId() {
    	String[] resourceParts = this.resourceUri.split("/");
    	return Long.valueOf(resourceParts[resourceParts.length-1]);
    }

}
