package ca.hashbrown.snapable.api.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import java.util.Date;

public class Event extends com.snapable.api.private_v1.objects.Event implements Parcelable {

    // fields
    public static final String FIELD_ID = BaseColumns._ID;
    public static final String FIELD_ENABLED = "enabled";
    public static final String FIELD_END = "end";
    public static final String FIELD_PHOTO_COUNT = "photo_count";
    public static final String FIELD_PIN = "pin";
    public static final String FIELD_PUBLIC = "is_public";
    public static final String FIELD_RESOURCE_URI = "resource_uri";
    public static final String FIELD_START = "start";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_URL = "url";

    // required for a Cursor implementation
    public static final String[] COLUMN_NAMES = {
    	FIELD_ID,
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

    // Android parcelable
    public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.enabled ? 1 : 0);
		dest.writeLong(this.end.getTime());
		dest.writeLong(this.photo_count);
		dest.writeString(this.pin);
		dest.writeInt(this.is_public ? 1 : 0);
		dest.writeString(this.resource_uri);
		dest.writeLong(this.start.getTime());
		dest.writeString(this.title);
		dest.writeString(this.url);
	}

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        public Event createFromParcel(Parcel in) {
            Event event = new Event();

            //event.setCover(in.readString());
            event.enabled = (in.readInt() == 0) ? false : true;
            event.end = new Date(in.readLong());
            event.photo_count = in.readLong();
            event.pin = in.readString();
            event.is_public = (in.readInt() == 0) ? false : true;
            event.resource_uri = in.readString();
            event.start = new Date(in.readLong());
            event.title = in.readString();
            event.url = in.readString();

            return event;
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

}