package ca.hashbrown.snapable.cursors;

import java.util.Date;

import com.snapable.api.models.Photo;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;

public class PhotoCursor extends MatrixCursor {
	
	private static final String TAG = "PhotoCursor";
	
	public PhotoCursor() {
		super(Photo.COLUMN_NAMES);
	}
	
	public PhotoCursor(Cursor c) {
		super(c.getColumnNames());
		int position = c.getPosition();

		// move to the start position and iterate through the cursor
		c.moveToPosition(-1);
		while (c.moveToNext()) {
			MatrixCursor.RowBuilder row = this.newRow();
	
			// add the values for the row
			for (String column : c.getColumnNames()) {
				Log.i(TAG, column);
				if (column.equals(Photo.FIELD_ID)) { row.add(c.getLong(c.getColumnIndex(Photo.FIELD_ID))); }
				if (column.equals(Photo.FIELD_AUTHOR_NAME)) { row.add(c.getString(c.getColumnIndex(Photo.FIELD_AUTHOR_NAME))); }
			}
		}
		
		// move to the position the original cursor was at
		this.moveToPosition(position);
	}
	
	public void add(Photo photo) {
		// create a new row
		MatrixCursor.RowBuilder row = this.newRow();
		
		// add the values for the row
		for (String column : getColumnNames()) {
			if (column.equals(Photo.FIELD_ID)) { row.add(photo.getId()); }
			if (column.equals(Photo.FIELD_AUTHOR_NAME)) { row.add(photo.getAuthorName()); }
		}

	}

	public Photo getPhoto() {
		checkPosition();
		Photo photo = new Photo();

		// populate the object
		photo.setAuthorName(this.getString(this.getColumnIndex(Photo.FIELD_AUTHOR_NAME)));

		return photo;
	}

}
