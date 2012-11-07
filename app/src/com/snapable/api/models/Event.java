package com.snapable.api.models;

import java.util.Date;

import org.codegist.common.lang.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

public class Event {
	@JsonProperty("cover")
    private long cover;
	@JsonProperty("enabled")
    private boolean isEnabled;
	@JsonProperty("end")
	private Date end;
	@JsonProperty("photo_count")
    private long photoCount;
	@JsonProperty("pin")
    private String pin;
	@JsonProperty("public")
    private boolean isPublic;
	@JsonProperty("resource_uri")
	private String resourceUri;
	@JsonProperty("start")
	private Date start;
    @JsonProperty("title")
    private String title;
    @JsonProperty("url")
    private String url;
    
    public String toString() {
        return new ToStringBuilder(this)
        	.append("cover", this.cover)
        	.append("isEnabled", this.isEnabled)
        	.append("end", this.end)
        	.append("photoCount", this.photoCount)
        	.append("pin", this.pin)
        	.append("isPublic", this.isPublic)
            .append("resourceUri", this.resourceUri)
            .append("start", start)
            .append("title", this.title)
            .append("url", this.url)
            .toString();
    }

    // properties
    public long getCover() {
        return this.cover;
    }

    public void setCover(long cover) {
        this.cover = cover;
    }
    
    public boolean getIsEnabled() {
        return this.isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    
    public Date getEnd() {
        return this.end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public long getPhotoCount() {
        return this.photoCount;
    }

    public void setPhotoCount(long photoCount) {
        this.photoCount = photoCount;
    }
    
    public String getPin() {
        return this.pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
    
    public boolean getIsPublic() {
        return this.isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public String getResourceUri() {
        return this.resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }
    
    public Date getStart() {
        return this.start;
    }

    public void setStart(Date start) {
        this.start = start;
    }
    
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    // virtual properties
    public long getId() {
    	String[] resourceParts = this.resourceUri.split("/");
    	return Long.valueOf(resourceParts[resourceParts.length-1]);
    }

}
