package com.snapable.api.model;

import org.codegist.common.lang.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

public class Photo {
    @JsonProperty("caption")
    private String caption;
    @JsonProperty("resource_uri")
    private String resourceUri;
    
    public String toString() {
        return new ToStringBuilder(this)
                .append("caption", this.caption)
                .append("resourceUri", resourceUri)
                .toString();
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

