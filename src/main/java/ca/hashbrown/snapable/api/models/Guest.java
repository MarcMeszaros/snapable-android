package ca.hashbrown.snapable.api.models;

import android.provider.BaseColumns;
import com.google.gson.annotations.SerializedName;
import com.snapable.api.ToStringBuilder;

public class Guest extends com.snapable.api.private_v1.objects.Guest {

	// fields
    public static final String FIELD_ID = BaseColumns._ID;
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_EVENT_URI = "event_uri";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_RESOURCE_URI = "resource_uri";

    // required for a Cursor implementation
    public static final String[] COLUMN_NAMES = {
    	FIELD_ID,
    	FIELD_EMAIL,
    	FIELD_EVENT_URI,
    	FIELD_NAME,
    	FIELD_RESOURCE_URI
    };

}
