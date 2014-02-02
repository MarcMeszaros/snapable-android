package ca.hashbrown.snapable.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * An Android Content Provider contract definition class. URI's are loosely based on
 * the equivalent resource they call when accessing data.
 * 
 * @author Marc Meszaros (marc@snapable.com)
 */
public final class SnapableContract {
	
	/** The authority for the snapable provider */
	public static final String AUTHORITY = "ca.hashbrown.snapable.provider";
	/** A content:// style uri to the authority for the snapable provider */
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

	public static final class Event implements EventColumns {
		/** @deprecated use {@link #CONTENT_URI} */
		@Deprecated
		public static final String RESOURCE_NAME = "event";
		/** A content:// style uri to the event content */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "event");
	}
	
	public static interface EventColumns {
		/**
         * The {@link Photo} to use as the cover image.
         * <p>TYPE: INTEGER</p>
		 */
		public static final String COVER = "cover";
		
		/**
		 * Is the event enabled.
		 * <p>TYPE: BOOLEAN</p>
		 */
		public static final String ENABLED = "enabled";
		
		/**
		 * The end time of the event.
		 * <p>TYPE: TEXT (ISO8601)</p>
		 */
		public static final String END = "end";

		/**
		 * The number of photos associated with the event.
		 * <p>TYPE: INTEGER</p>
		 */
		public static final String PHOTO_COUNT = "photo_count";

		/**
		 * The event PIN.
		 * <p>TYPE: TEXT</p>
		 */
		public static final String PIN = "pin";
		
		/**
		 * If the event is public or private.
		 * <p>TYPE: BOOLEAN</p>
		 */
		public static final String PUBLIC = "public";
		
		/**
		 * The start time of the event.
		 * <p>TYPE: TEXT (ISO8601)</p>
		 */
		public static final String START = "start";

		/**
		 * The event title.
		 * <p>TYPE: TEXT</p>
		 */
		public static final String TITLE = "title";

		/** 
		 * @deprecated use {@link #PUBLIC}
		 */
		@Deprecated
		public static final String TYPE = "type";
		
		/**
		 * The event URL identifier to be used with <i>http://snapable.com/event/<b>URL</b></i>.
		 * <p>TYPE: TEXT</p>
		 */
		public static final String URL = "url";
	}
	
	
	public static final class Photo implements PhotoColumns {
		/** @deprecated use {@link #CONTENT_URI} */
		@Deprecated
		public static final String RESOURCE_NAME = "photo";
		/** A content:// style uri to the photo content */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "photo");
	}
	
	public static interface PhotoColumns {
		/**
		 * The photographer's name.
		 * <p>TYPE: TEXT</p>
		 */
		public static final String AUTHOR_NAME = "author_name";
		
		/**
		 * The image caption specified by the uploader.
		 * <p>TYPE: TEXT</p>
		 */
		public static final String CAPTION = "caption";
		
		/**
		 * Is the photo allowed to be streamed.
		 * <p>TYPE: BOOLEAN</p>
		 */
		public static final String STREAMABLE = "streamable";
	}

	public static final class Guest {
		/** @deprecated use {@link #CONTENT_URI} */
		@Deprecated
		public static final String RESOURCE_NAME = "guest";
		/** A content:// style uri to the guest content */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "guest");
	}
	
	public static final class EventCredentials implements EventCredentialsColumns {
		/** @deprecated use {@link #CONTENT_URI} */
		@Deprecated
		public static final String RESOURCE_NAME = "event_credentials";
		/** A content:// style uri to the guest event credentials content */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "event_credentials");
	}
	
	public static interface EventCredentialsColumns extends BaseColumns {
		/**
		 * The guest email for the event.
		 * <p>TYPE: TEXT</p>
		 */
		public static final String EMAIL = "email";
		
		/**
		 * The guest id from the Snapable API (if available).
		 * <p>TYPE: INTEGER</p>
		 */
		public static final String GUEST_ID = "guest_id";
		
		/**
		 * The guest's name to display.
		 * <p>TYPE: TEXT</p>
		 */
		public static final String NAME = "name";
		
		/**
		 * The event pin used to login to the event.
		 * <p>TYPE: TEXT</p>
		 */
		public static final String PIN = "pin";
		
		/**
		 * The the guest type.
		 * <p>TYPE: INTEGER</p>
		 */
		public static final String TYPE_ID = "type_id";
	}
}
