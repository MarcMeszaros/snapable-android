package ca.hashbrown.snapable.provider;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.hashbrown.snapable.cursors.*;

import com.snapable.api.SnapApi;
import com.snapable.api.SnapClient;
import com.snapable.api.models.*;
import com.snapable.api.resources.*;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.util.Log;

public class SnapContentProvider extends ContentProvider {

	private static final String TAG = "SnapContentProvider";

	public static final String AUTHORITY = SnapableContract.AUTHORITY;
	
	/*
	 * Uri matching static variables.
	 * NOTE 1: the numbers are internal to this class (the actual value doesn't really matter)
	 * NOTE 2: resource/endpoint id's in the convention are completely arbitrary because of NOTE 1
	 * 
	 * CONVENTION (in case you are trying to figure out the pattern...):
	 * 1xyy = API (x = resource id, y = endpoint id)
	 * 2xyy = DB (x = resource id, y = endpoint id)
	*/
	
	/* ==== API ==== */
	//private static final int ACCOUNT = 1101;
	//private static final int ACCOUNT_ID = 1102;

	private static final int EVENT = 1201;
	private static final int EVENT_ID = 1202;

	private static final int GUEST = 1301;
	private static final int GUEST_ID = 1302;

	private static final int PHOTO = 1401;
	private static final int PHOTO_ID = 1402;

	//private static final int USER = 1501;
	//private static final int USER_ID = 1502;

	/* ==== DB ==== */
	private static final int EVENT_CREDENTIALS = 2101;
	private static final int EVENT_CREDENTIALS_ID = 2102;

	// build the uri matcher from the codes above
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		
		/* == API == */
		//uriMatcher.addURI(AUTHORITY, "account", ACCOUNT);
		//uriMatcher.addURI(AUTHORITY, "account/#", ACCOUNT_ID);

		uriMatcher.addURI(AUTHORITY, "event", EVENT);
		uriMatcher.addURI(AUTHORITY, "event/#", EVENT_ID);
		
		//uriMatcher.addURI(AUTHORITY, "guest", GUEST);
		//uriMatcher.addURI(AUTHORITY, "guest/#", GUEST_ID);

		uriMatcher.addURI(AUTHORITY, "photo", PHOTO);
		uriMatcher.addURI(AUTHORITY, "photo/#", PHOTO_ID);
		
		//uriMatcher.addURI(AUTHORITY, "user", USER);
		//uriMatcher.addURI(AUTHORITY, "user/#", USERS_ID);
		
		/* == DB == */
		uriMatcher.addURI(AUTHORITY, "event_credentials", EVENT_CREDENTIALS);
		uriMatcher.addURI(AUTHORITY, "event_credentials/#", EVENT_CREDENTIALS_ID);
	}
	
	// class variable
	private SnapClient snapClient;
	private DBHelper dbHelper;

	@Override
	public boolean onCreate() {
		snapClient = SnapClient.getInstance();
		dbHelper = new DBHelper(getContext());

		// return success or failure
		if (snapClient != null && dbHelper != null) {
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
		// create an SQL object
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		// set/initiate some objects
		Uri result = null;

		switch (uriMatcher.match(uri)) {
			case EVENT_CREDENTIALS: {
				// TODO remove this dirty hack
				// update/create a guest via API if there is an email
				if (!values.getAsString(SnapableContract.EventCredentials.EMAIL).isEmpty()) {
					UpdateInsertGuest task = new UpdateInsertGuest(values);
					task.execute();
				}
				
				result = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, db.insert(DBHelper.EVENT_CREDENTIALS.TABLE_NAME, null, values));
				break;
			}

			default: {
				return null;
			}
		}
		
		// we successfully inserted the row, close the db, notify of change and return
		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// create the empty results cursor
		Cursor result = null;
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

			case EVENT_CREDENTIALS_ID: {
				String dbSelect = DBHelper.EVENT_CREDENTIALS.FIELD_ID + " = ?";
				String dbArgs[] = { Long.toString(ContentUris.parseId(uri)) };

				// notify of data change and return the result
				SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
				builder.setTables(DBHelper.EVENT_CREDENTIALS.TABLE_NAME);
				result = builder.query(dbHelper.getReadableDatabase(), null, dbSelect, dbArgs, null, null, null);
				break;
			}
		}

		result.setNotificationUri(getContext().getContentResolver(), uri);
		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// create an SQL object and initialize some objects
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int rowsAffected = 0;
		
		switch (uriMatcher.match(uri)) {
			case EVENT_CREDENTIALS_ID: {
				rowsAffected = db.update(DBHelper.EVENT_CREDENTIALS.TABLE_NAME, values, null, null);
				break;
			}
			default: {
				rowsAffected = 0;
				break;
			}
		}

		// we successfully updated, close the db, notify of change and return
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsAffected;
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
	
	/**
	 * Nested class for managing the SQL database.
	 */
	private static class DBHelper extends SQLiteOpenHelper {

		private static final int DATABASE_VERSION = 1;

		public DBHelper(Context context) {
			super(context, "snapable.db", null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// create the tables
			createEventCredentialsTable(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// API documentation recommendations
			/*
			if (oldVersion == 1) {
				// do upgrade stuff
				oldVersion++;
			}
			*/
		}
		
		/**
		 * Creates the event credentials table in the database.
		 */
		private void createEventCredentialsTable(SQLiteDatabase db) {
			//CREATE TABLE IF NOT EXISTS event_credentials(id INT PRIMARY KEY, guest_id INT, email TEXT, name TEXT, pin TEXT)
			db.execSQL("CREATE TABLE " + DBHelper.EVENT_CREDENTIALS.TABLE_NAME + " ("
				+ DBHelper.EVENT_CREDENTIALS.FIELD_ID + " INTEGER PRIMARY KEY,"
				+ DBHelper.EVENT_CREDENTIALS.FIELD_GUEST_ID + " INTEGER,"
				+ DBHelper.EVENT_CREDENTIALS.FIELD_EMAIL + " TEXT,"
				+ DBHelper.EVENT_CREDENTIALS.FIELD_NAME + " TEXT,"
				+ DBHelper.EVENT_CREDENTIALS.FIELD_PIN + " TEXT,"
				+ DBHelper.EVENT_CREDENTIALS.FIELD_TYPE_ID + " INTEGER"
			+ ");");	
		}

		// ====== Table Field Definitions ====== \\
		/**
		 * A nested class defining various attributes of the "event_credentials" table.
		 */
		public static final class EVENT_CREDENTIALS {
			// map the DB columns to fields in the content provider
			public static final String TABLE_NAME = "event_credentials";
			public static final String FIELD_ID = BaseColumns._ID;
			public static final String FIELD_GUEST_ID = "guest_id";
			public static final String FIELD_EMAIL = "email";
			public static final String FIELD_NAME = "name";
			public static final String FIELD_PIN = "pin";
			public static final String FIELD_TYPE_ID = "type_id";
		}

	}
	
	////// UGLY HACK
	private class UpdateInsertGuest extends AsyncTask<Void, Void, Void> {

		private ContentValues values;
		
		public UpdateInsertGuest(ContentValues values) {
			this.values = values;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// some values
			String guest_email = values.getAsString(SnapableContract.EventCredentials.EMAIL);
			String guest_event = "/" + SnapApi.api_version + "/event/" + values.getAsLong(SnapableContract.EventCredentials._ID) + "/";
			String guest_name = values.getAsString(SnapableContract.EventCredentials.NAME);
			String guest_type = "/" + SnapApi.api_version + "/type/" + values.getAsInteger(SnapableContract.EventCredentials.TYPE_ID) + "/";

			// setup the API client
			GuestResource guestResource = SnapClient.getInstance().build(GuestResource.class);
			Pager<Guest[]> guests = guestResource.getGuests(guest_email, values.getAsLong(SnapableContract.EventCredentials._ID));
			
			// if we have a guest update the result
			if (guests.getMeta().getTotalCount() == 1) {
				guestResource.putGuest(guests.getObjects()[0].getId(), guest_name);
			} else {
				// create the guest
				Guest guest = guestResource.postGuest(guest_event, guest_type, guest_email, guest_name);
			}
			
			return null;
		}
		
	}

}
