package ca.hashbrown.snapable.provider;


import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.hashbrown.snapable.cursors.*;

import com.snapable.api.SnapClient;
import com.snapable.api.models.*;
import com.snapable.api.resources.*;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
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
	//private static final int ACCOUNT = 1001;
	//private static final int ACCOUNT_ID = 1002;
	
	private static final int EVENT = 2001;
	private static final int EVENT_ID = 2002;

	private static final int GUEST = 3001;
	private static final int GUEST_ID = 3002;
	
	private static final int PHOTO = 4001;
	private static final int PHOTO_ID = 4002;
	
	//private static final int USER = 5001;
	//private static final int USER_ID = 5002;

	// build the uri matcher from the codes above
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		//uriMatcher.addURI(AUTHORITY, SnapableContract.Account.RESOURCE_NAME, ACCOUNT);
		//uriMatcher.addURI(AUTHORITY, SnapableContract.Account.RESOURCE_NAME+"/#", ACCOUNT_ID);
		
		uriMatcher.addURI(AUTHORITY, SnapableContract.Event.RESOURCE_NAME, EVENT);
		uriMatcher.addURI(AUTHORITY, SnapableContract.Event.RESOURCE_NAME+"/#", EVENT_ID);
		
		//uriMatcher.addURI(AUTHORITY, GuestResource.RESOURCE_NAME, GUEST);
		//uriMatcher.addURI(AUTHORITY, GuestResource.RESOURCE_NAME+"/#", GUEST_ID);

		uriMatcher.addURI(AUTHORITY, PhotoResource.RESOURCE_NAME, PHOTO);
		uriMatcher.addURI(AUTHORITY, PhotoResource.RESOURCE_NAME+"/#", PHOTO_ID);
		
		//uriMatcher.addURI(AUTHORITY, UserResource.RESOURCE_NAME, USER);
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
		case EVENT:
			return ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.com.snapable.api.event";
		case EVENT_ID:
			return ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.com.snapable.api.event";
		case GUEST:
			return ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.com.snapable.api.guest";
		case GUEST_ID:
			return ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.com.snapable.api.guest";
		case PHOTO:
			return ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.com.snapable.api.photo";
		case PHOTO_ID:
			return ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.com.snapable.api.photo";
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
		MatrixCursor result = null;
		EventResource eventRes = snapClient.build(EventResource.class);
		PhotoResource photoRes = snapClient.build(PhotoResource.class);
		
		switch (uriMatcher.match(uri)) {
			// handle the case for all events
			case EVENT: {
				// set the column names or a default
				EventCursor eventsCursor = (projection != null) ? new EventCursor(projection) : new EventCursor();
				
				Pager<Event[]> events = null;
				HashMap<String, String> hMap = getHashmap(selection, selectionArgs);
				if (hMap != null && hMap.containsKey("lat") && hMap.containsKey("lng")) {
					float lat =  Float.parseFloat(hMap.get("lat"));
					float lng =  Float.parseFloat(hMap.get("lng"));

					events = eventRes.getEvents(lat, lng);
				} else if (hMap != null && hMap.containsKey("q")) {
					String query = hMap.get("q");
					events = eventRes.getEvents(query);
				} else {
					// make the api call
					events = eventRes.getEvents();
				}
				// add the event objects to the resulting cursor
				for (Event event : events.getObjects()) {
					eventsCursor.add(event);
				}
	
				// set or temporary cursor as the return cursor
				result = eventsCursor;
				break;
			}

			case EVENT_ID: {
				// set the column names or a default
				EventCursor eventCursor = (projection != null) ? new EventCursor(projection) : new EventCursor();
				
				// make the api call
				Event event = eventRes.getEvent(ContentUris.parseId(uri));
				
				// add the event objects to the resulting cursor
				eventCursor.add(event);
	
				// set or temporary cursor as the return cursor
				result = eventCursor;
				break;
			}

			case PHOTO: {
				// set tge column names or a default
				PhotoCursor photosCursor = new PhotoCursor();
			
				// make the API call
				Pager<Photo[]> photos = (selectionArgs.length == 1) ? photoRes.getPhotos(Long.valueOf(selectionArgs[0])) : photoRes.getPhotos();
				
				// add the event objects to the resulting cursor
				for (Photo photo : photos.getObjects()) {
					photosCursor.add(photo);
				}
	
				// set or temporary cursor as the return cursor
				result = photosCursor;
				break;
			}

			case PHOTO_ID: {
				break;
			}
		}
		
		result.setNotificationUri(getContext().getContentResolver(), uri);
		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	// http://stackoverflow.com/questions/12949730/contentprovider-implementation-how-to-convert-selection-and-selectionargs
	public static HashMap<String, String> getHashmap(String selection, String[] selectionArgs) {
		try {
			HashMap<String, String> result = new HashMap<String, String>();
		
		    Pattern pattern = Pattern.compile("[a-z]*(\\s)*=\\?", Pattern.CASE_INSENSITIVE);
		    Matcher matcher = pattern.matcher(selection);
		
		    int pos = 0;
		    while (matcher.find()) {
		        String[] selParts = matcher.group(0).split("=");
		        result.put(selParts[0], selectionArgs[pos]);
		        pos++;
		    }
		
		    return result;
	    } catch (Exception e) {
	    	Log.e(TAG, "error creating hashmap from selection string", e);
	    	return null;
	    }
	}

}
