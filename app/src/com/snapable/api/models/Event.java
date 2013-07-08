package com.snapable.api.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import org.codegist.common.lang.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class Event implements Parcelable {
	//@JsonProperty("cover")
    //private String cover;
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

    // fields
    public static final String FIELD_ID = BaseColumns._ID;
    //public static final String FIELD_COVER = "cover";
    public static final String FIELD_ENABLED = "isEnabled";
    public static final String FIELD_END = "end";
    public static final String FIELD_PHOTO_COUNT = "photoCount";
    public static final String FIELD_PIN = "pin";
    public static final String FIELD_PUBLIC = "isPublic";
    public static final String FIELD_RESOURCE_URI = "resourceUri";
    public static final String FIELD_START = "start";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_URL = "url";

    // required for a Cursor implementation
    public static final String[] COLUMN_NAMES = {
    	FIELD_ID,
    	//FIELD_COVER,
    	FIELD_ENABLED,
    	FIELD_END,
    	FIELD_PHOTO_COUNT,
    	FIELD_PIN,
    	FIELD_PUBLIC,
    	FIELD_RESOURCE_URI,
    	FIELD_START,
    	FIELD_TITLE,
    	FIELD_URL
    };

    public String toString() {
        return new ToStringBuilder(this)
        	//.append(FIELD_COVER, this.cover)
        	.append(FIELD_ENABLED, this.isEnabled)
        	.append(FIELD_END, this.end)
        	.append(FIELD_PHOTO_COUNT, this.photoCount)
        	.append(FIELD_PIN, this.pin)
        	.append(FIELD_PUBLIC, this.isPublic)
            .append(FIELD_RESOURCE_URI, this.resourceUri)
            .append(FIELD_START, start)
            .append(FIELD_TITLE, this.title)
            .append(FIELD_URL, this.url)
            .toString();
    }

    // properties
    /*
    public String getCover() {
        return this.cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
    */

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

    // Android parcelable
	/*
    private Event(Parcel in) {
        this.setCover(in.readLong());
        this.setIsEnabled((in.readInt() == 0) ? false : true);
        this.setEnd(new Date(in.readLong()));
        this.setPhotoCount(in.readLong());
        this.setPin(in.readString());
        this.setIsPublic((in.readInt() == 0) ? false : true);
        this.setResourceUri(in.readString());
        this.setStart(new Date(in.readLong()));
        this.setTitle(in.readString());
        this.setUrl(in.readString());
    }
    */

    public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		//dest.writeString(this.getCover());
		dest.writeInt(this.getIsEnabled() ? 1 : 0);
		dest.writeLong(this.getEnd().getTime());
		dest.writeLong(this.getPhotoCount());
		dest.writeString(this.getPin());
		dest.writeInt(this.getIsPublic() ? 1 : 0);
		dest.writeString(this.getResourceUri());
		dest.writeLong(this.getStart().getTime());
		dest.writeString(this.getTitle());
		dest.writeString(this.getUrl());
	}

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        public Event createFromParcel(Parcel in) {
            Event event = new Event();

            //event.setCover(in.readString());
            event.setIsEnabled((in.readInt() == 0) ? false : true);
            event.setEnd(new Date(in.readLong()));
            event.setPhotoCount(in.readLong());
            event.setPin(in.readString());
            event.setIsPublic((in.readInt() == 0) ? false : true);
            event.setResourceUri(in.readString());
            event.setStart(new Date(in.readLong()));
            event.setTitle(in.readString());
            event.setUrl(in.readString());

            return event;
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

}
