package ca.hashbrown.snapable.api.models;

import android.provider.BaseColumns;

public class Photo extends com.snapable.api.private_v1.objects.Photo {

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

}

