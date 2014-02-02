package ca.hashbrown.snapable.cursors;

import android.database.Cursor;
import android.database.MatrixCursor;
import com.snapable.api.models.Photo;

import java.util.Date;

public class PhotoCursor extends MatrixCursor {

	/**
	 * Create a new PhotoCursor.
	 */
	public PhotoCursor() {
		super(Photo.COLUMN_NAMES);
	}

	/**
	 * Create a new PhotoCursor.
	 *
	 * @param columnNames The list of columns for the cursor.
	 */
	public PhotoCursor(String[] columnNames) {
		super(Photo.COLUMN_NAMES);
	}

	/**
	 * Create a new PhotoCursor using a regular Cursor.
	 *
	 * @param c The cursor to convert into an PhotoCursor
	 */
	public PhotoCursor(Cursor c) {
		super(c.getColumnNames());
		int position = c.getPosition();

		// move to the start position and iterate through the cursor
		c.moveToPosition(-1);
		while (c.moveToNext()) {
			MatrixCursor.RowBuilder row = this.newRow();

			// add the values for the row
			for (String column : c.getColumnNames()) {
				if (column.equals(Photo.FIELD_ID)) { row.add(c.getLong(c.getColumnIndex(Photo.FIELD_ID))); }
				if (column.equals(Photo.FIELD_AUTHOR_NAME)) { row.add(c.getString(c.getColumnIndex(Photo.FIELD_AUTHOR_NAME))); }
				if (column.equals(Photo.FIELD_CAPTION)) { row.add(c.getString(c.getColumnIndex(Photo.FIELD_CAPTION))); }
				if (column.equals(Photo.FIELD_EVENT_URI)) { row.add(c.getString(c.getColumnIndex(Photo.FIELD_EVENT_URI))); }
				if (column.equals(Photo.FIELD_RESOURCE_URI)) { row.add(c.getString(c.getColumnIndex(Photo.FIELD_RESOURCE_URI))); }
				if (column.equals(Photo.FIELD_STREAMABLE)) { row.add(c.getInt(c.getColumnIndex(Photo.FIELD_STREAMABLE))); }
				if (column.equals(Photo.FIELD_TIMESTAMP)) { row.add(c.getLong(c.getColumnIndex(Photo.FIELD_TIMESTAMP))); }
			}
		}

		// move to the position the original cursor was at
		this.moveToPosition(position);
	}

	/**
	 * Add a Photo object to the Cursor.
	 *
	 * @param photo The photo to add to the cursor.
	 */
	public void add(Photo photo) {
		// create a new row
		MatrixCursor.RowBuilder row = this.newRow();

		// add the values for the row
		for (String column : getColumnNames()) {
			if (column.equals(Photo.FIELD_ID)) { row.add(photo.getId()); }
			if (column.equals(Photo.FIELD_AUTHOR_NAME)) { row.add(photo.author_name); }
			if (column.equals(Photo.FIELD_CAPTION)) { row.add(photo.caption); }
			if (column.equals(Photo.FIELD_EVENT_URI)) { row.add(photo.event_uri); }
			if (column.equals(Photo.FIELD_RESOURCE_URI)) { row.add(photo.resource_uri); }
			if (column.equals(Photo.FIELD_STREAMABLE)) { row.add((photo.streamable) ? 1 : 0); }
			if (column.equals(Photo.FIELD_TIMESTAMP)) { row.add(photo.timestamp.getTime()); }
		}

	}

	/**
	 * Get a photo object at the position the cursor is currently pointing.
	 *
	 * @return a Photo object at the current cursor position
	 */
	public Photo getPhoto() {
		checkPosition();
		Photo photo = new Photo();

		// populate the object
		photo.author_name = this.getString(this.getColumnIndex(Photo.FIELD_AUTHOR_NAME));
		photo.caption = this.getString(this.getColumnIndex(Photo.FIELD_CAPTION));
		photo.event_uri = this.getString(this.getColumnIndex(Photo.FIELD_EVENT_URI));
		photo.resource_uri = this.getString(this.getColumnIndex(Photo.FIELD_RESOURCE_URI));
		photo.streamable = (this.getInt(this.getColumnIndex(Photo.FIELD_STREAMABLE)) == 0) ? false : true;
		photo.timestamp = new Date(this.getLong(this.getColumnIndex(Photo.FIELD_TIMESTAMP)));

		return photo;
	}

}
