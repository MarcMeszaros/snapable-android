package ca.hashbrown.snapable.provider;

import android.net.Uri;

/**
 * An Android Content Provider contract definition class. URI's are loosely based on
 * the equivalent API resource they call when accessing remote data.
 * 
 * @author Marc Meszaros (marc@snapable.com)
 */
public class SnapableContract {
	
	public static final String AUTHORITY = "ca.hashbrown.snapable.provider";
	
	/*
	public static final class Account {
		public static final String RESOURCE_NAME = "account";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + RESOURCE_NAME );
	}
	*/
	
	public static final class Event {
		public static final String RESOURCE_NAME = "event";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + RESOURCE_NAME );
	}
	
	public static final class Photo {
		public static final String RESOURCE_NAME = "photo";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + RESOURCE_NAME );
	}

	public static final class Guest {
		public static final String RESOURCE_NAME = "guest";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + RESOURCE_NAME );
	}

	/*
	public static final class User {
		public static final String RESOURCE_NAME = "user";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + RESOURCE_NAME );
	}
	*/
}
