package ca.hashbrown.snapable.provider;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.util.Log;

import com.snapable.api.private_v1.Client;
import com.snapable.api.private_v1.objects.Guest;
import com.snapable.api.private_v1.objects.Pager;
import com.snapable.api.private_v1.resources.GuestResource;

import ca.hashbrown.snapable.BuildConfig;
import ca.hashbrown.snapable.api.SnapClient;

public class SnapContentProvider extends ContentProvider {

	private static final String TAG = "SnapContentProvider";

	public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

	/*
	 * Uri matching static variables.
	 * NOTE 1: the numbers are internal to this class (the actual value doesn't really matter)
	 * NOTE 2: resource/endpoint id's in the convention are completely arbitrary because of NOTE 1
	 *
	 * CONVENTION (in case you are trying to figure out the pattern...):
	 * 1xyy = API (x = resource id, y = endpoint id)
	 * 2xyy = DB (x = resource id, y = endpoint id)
	*/

	/* ==== DB ==== */
	private static final int EVENT_CREDENTIALS = 2101;
	private static final int EVENT_CREDENTIALS_ID = 2102;

	// build the uri matcher from the codes above
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		/* == DB == */
		uriMatcher.addURI(AUTHORITY, "event_credentials", EVENT_CREDENTIALS);
		uriMatcher.addURI(AUTHORITY, "event_credentials/#", EVENT_CREDENTIALS_ID);
	}

	// class variable
	private Client snapClient;
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
		return "";
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
				result = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, db.insert(DBHelper.EVENT_CREDENTIALS.TABLE_NAME, null, values));

				// TODO remove this dirty hack
				// update/create a guest via API if there is an email
				if (!values.getAsString(SnapableContract.EventCredentials.EMAIL).isEmpty()) {
					UpdateInsertGuest task = new UpdateInsertGuest(values);
					task.execute();
				}
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

		switch (uriMatcher.match(uri)) {

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
	public int update(final Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// create an SQL object and initialize some objects
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int rowsAffected = 0;

		switch (uriMatcher.match(uri)) {
			case EVENT_CREDENTIALS_ID: {
                rowsAffected = db.update(DBHelper.EVENT_CREDENTIALS.TABLE_NAME, values, null, null);
                // if we have a guest update the result
                final GuestResource guestResource = snapClient.getRestAdapter().create(GuestResource.class);
                final Guest guestPost = new Guest();
                guestPost.setEvent(ContentUris.parseId(uri));
                if (values.containsKey(SnapableContract.EventCredentials.NAME))
                    guestPost.name = values.getAsString(SnapableContract.EventCredentials.NAME);
                if (values.containsKey(SnapableContract.EventCredentials.EMAIL))
                    guestPost.email = values.getAsString(SnapableContract.EventCredentials.EMAIL);

                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            guestResource.putGuest(ContentUris.parseId(uri), guestPost);
                        }
                    }).start();
                } catch (Exception ignored) {}
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
			String guest_event = "/" + snapClient.VERSION + "/event/" + values.getAsLong(SnapableContract.EventCredentials._ID) + "/";
			String guest_name = values.getAsString(SnapableContract.EventCredentials.NAME);

			// setup the API client
			GuestResource guestResource = snapClient.getRestAdapter().create(GuestResource.class);
			Pager<Guest> guests = guestResource.getGuests(guest_email, values.getAsLong(SnapableContract.EventCredentials._ID));

			// if we have a guest update the result
			if (guests.meta.totalCount == 1) {
                Guest guestPost = new Guest();
                guestPost.name = guest_name;
				guestResource.putGuest(guests.objects.get(0).getPk(), guestPost);

				// update the local db with the guest id
				Uri request_uri = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, values.getAsLong(SnapableContract.EventCredentials._ID));
				ContentValues vals = new ContentValues();
				vals.put(SnapableContract.EventCredentials.GUEST_ID, guests.objects.get(0).getPk());
				getContext().getContentResolver().update(request_uri, vals, null, null);
			} else {
				// create the guest and update
                Guest guestPost = new Guest();
                guestPost.eventUri = guest_event;
                guestPost.email = guest_email;
                guestPost.name = guest_name;
				Guest guest = guestResource.postGuest(guestPost);

				// update the local db with the guest id
				Uri request_uri = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, values.getAsLong(SnapableContract.EventCredentials._ID));
				ContentValues vals = new ContentValues();
				vals.put(SnapableContract.EventCredentials.GUEST_ID, guest.getPk());
				getContext().getContentResolver().update(request_uri, vals, null, null);
			}

			return null;
		}

	}

}
