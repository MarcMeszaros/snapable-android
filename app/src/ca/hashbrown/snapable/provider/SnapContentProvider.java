package ca.hashbrown.snapable.provider;


import com.snapable.api.SnapClient;
import com.snapable.api.models.*;
import com.snapable.api.resources.*;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

public class SnapContentProvider extends ContentProvider {

	private static final String TAG = "SnapContentProvider";

	public static final String AUTHORITY = SnapableContract.AUTHORITY;
	
	// uri matching static variables
	// NOTE: the numbers are internal to this class (the actual value doesn't really matter)
	//private static final int ACCOUNTS = 1001;
	//private static final int ACCOUNT_ID = 1002;
	
	private static final int EVENTS = 2001;
	private static final int EVENT_ID = 2002;

	private static final int GUESTS = 3001;
	private static final int GUEST_ID = 3002;
	
	private static final int PHOTOS = 4001;
	private static final int PHOTO_ID = 4002;
	
	//private static final int USERS = 5001;
	//private static final int USER_ID = 5002;

	// build the uri matcher from the codes above
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		//uriMatcher.addURI(AUTHORITY, SnapableContract.Account.RESOURCE_NAME, ACCOUNTS);
		//uriMatcher.addURI(AUTHORITY, SnapableContract.Account.RESOURCE_NAME+"/#", ACCOUNT_ID);
		
		uriMatcher.addURI(AUTHORITY, SnapableContract.Event.RESOURCE_NAME, EVENTS);
		uriMatcher.addURI(AUTHORITY, SnapableContract.Event.RESOURCE_NAME+"/#", EVENT_ID);
		
		//uriMatcher.addURI(AUTHORITY, GuestResource.RESOURCE_NAME, GUESTS);
		//uriMatcher.addURI(AUTHORITY, GuestResource.RESOURCE_NAME+"/#", GUEST_ID);

		uriMatcher.addURI(AUTHORITY, PhotoResource.RESOURCE_NAME, PHOTOS);
		uriMatcher.addURI(AUTHORITY, PhotoResource.RESOURCE_NAME+"/#", PHOTO_ID);
		
		//uriMatcher.addURI(AUTHORITY, UserResource.RESOURCE_NAME, USERS);
		//uriMatcher.addURI(AUTHORITY, UserResource.RESOURCE_NAME+"/#", USERS_ID);
	}
	
	// class variable
	private SnapClient snapClient;

	@Override
	public boolean onCreate() {
		snapClient = SnapClient.getInstance();

		// return success or failure
		if (snapClient != null) {
			Log.d(TAG, "content provider created");
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case EVENTS:
			return "vnd.android.cursor.dir/vnd.com.snapable.api.event";
		case EVENT_ID:
			return "vnd.android.cursor.item/vnd.com.snapable.api.event";
		case GUESTS:
			return "vnd.android.cursor.dir/vnd.com.snapable.api.guest";
		case GUEST_ID:
			return "vnd.android.cursor.item/vnd.com.snapable.api.guest";
		case PHOTOS:
			return "vnd.android.cursor.dir/vnd.com.snapable.api.photo";
		case PHOTO_ID:
			return "vnd.android.cursor.item/vnd.com.snapable.api.photo";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// create the empty results cursor
		String[] columnNames = null;
		MatrixCursor result = null;
		
		switch (uriMatcher.match(uri)) {
		// handle the case for all events
		case EVENTS:
			// set the column names or a default
			//columnNames = (projection != null) ? projection : new String[]{"_id", "title"};
			columnNames = new String[]{"_id", "title", "photo_count"};
			result = new MatrixCursor(columnNames);
			
			// make the api call
			EventResource eventRes = snapClient.build(EventResource.class);
			Pager<Event[]> events = eventRes.getEvents();
			
			// add the event objects to the resulting cursor
			for (Event event : events.getObjects()) {
				result.addRow(new Object[]{event.getId(), event.getTitle(), event.getPhotoCount()});
			}
			
			break;
		}
		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
