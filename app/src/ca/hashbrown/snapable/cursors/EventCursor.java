package ca.hashbrown.snapable.cursors;

import java.util.Date;

import com.snapable.api.models.Event;

import android.database.Cursor;
import android.database.MatrixCursor;

public class EventCursor extends MatrixCursor {
	
	/**
	 * Create a new EventCursor.
	 */
	public EventCursor() {
		super(Event.COLUMN_NAMES);
	}

	/**
	 * Create a new EventCursor.
	 * 
	 * @param columnNames The list of columns for the cursor.
	 */
	public EventCursor(String[] columnNames) {
		super(Event.COLUMN_NAMES);
	}

	/**
	 * Create a new EventCursor using a regular Cursor.
	 * 
	 * @param cursor The cursor to convert into an EventCursor
	 */
	public EventCursor(Cursor c) {
		super(c.getColumnNames());
		int position = c.getPosition();

		// move to the start position and iterate through the cursor
		c.moveToPosition(-1);
		while (c.moveToNext()) {
			MatrixCursor.RowBuilder row = this.newRow();
	
			// add the values for the row
			for (String column : c.getColumnNames()) {
				if (column.equals(Event.FIELD_ID)) { row.add(c.getLong(c.getColumnIndex(Event.FIELD_ID))); }
				if (column.equals(Event.FIELD_COVER)) { row.add(c.getLong(c.getColumnIndex(Event.FIELD_COVER))); }
				if (column.equals(Event.FIELD_ENABLED)) { row.add(c.getInt(c.getColumnIndex(Event.FIELD_ENABLED))); }
				if (column.equals(Event.FIELD_END)) { row.add(c.getLong(c.getColumnIndex(Event.FIELD_END))); }
				if (column.equals(Event.FIELD_PHOTO_COUNT)) { row.add(c.getLong(c.getColumnIndex(Event.FIELD_PHOTO_COUNT))); }
				if (column.equals(Event.FIELD_PIN)) { row.add(c.getString(c.getColumnIndex(Event.FIELD_PIN))); }
				if (column.equals(Event.FIELD_PUBLIC)) { row.add(c.getInt(c.getColumnIndex(Event.FIELD_PUBLIC))); }
				if (column.equals(Event.FIELD_RESOURCE_URI)) { row.add(c.getString(c.getColumnIndex(Event.FIELD_RESOURCE_URI))); }
				if (column.equals(Event.FIELD_START)) { row.add(c.getLong(c.getColumnIndex(Event.FIELD_START))); }
				if (column.equals(Event.FIELD_TITLE)) { row.add(c.getString(c.getColumnIndex(Event.FIELD_TITLE))); }
				if (column.equals(Event.FIELD_URL)) { row.add(c.getString(c.getColumnIndex(Event.FIELD_URL))); }
			}
		}
		
		// move to the position the original cursor was at
		this.moveToPosition(position);
	}

	/**
	 * Add an Event object to the Cursor.
	 * 
	 * @param event The event to add to the cursor.
	 */
	public void add(Event event) {
		// create a new row
		MatrixCursor.RowBuilder row = this.newRow();
		
		// add the values for the row
		for (String column : getColumnNames()) {
			if (column.equals(Event.FIELD_ID)) { row.add(event.getId()); }
			if (column.equals(Event.FIELD_COVER)) { row.add(event.getCover()); }
			if (column.equals(Event.FIELD_ENABLED)) { row.add((event.getIsEnabled()) ? 1 : 0); }
			if (column.equals(Event.FIELD_END)) { row.add(event.getEnd().getTime()); }
			if (column.equals(Event.FIELD_PHOTO_COUNT)) { row.add(event.getPhotoCount()); }
			if (column.equals(Event.FIELD_PIN)) { row.add(event.getPin()); }
			if (column.equals(Event.FIELD_PUBLIC)) { row.add((event.getIsPublic()) ? 1 : 0); }
			if (column.equals(Event.FIELD_RESOURCE_URI)) { row.add(event.getResourceUri()); }
			if (column.equals(Event.FIELD_START)) { row.add(event.getStart().getTime()); }
			if (column.equals(Event.FIELD_TITLE)) { row.add(event.getTitle()); }
			if (column.equals(Event.FIELD_URL)) { row.add(event.getUrl()); }
		}

	}

	/**
	 * Get an event object at the position the cursor is currently pointing.
	 * 
	 * @return an Event object at the current cursor position
	 */
	public Event getEvent() {
		checkPosition();
		Event event = new Event();

		// populate the object
		event.setCover(this.getLong(this.getColumnIndex(Event.FIELD_COVER)));
		event.setIsEnabled((this.getInt(this.getColumnIndex(Event.FIELD_ENABLED)) == 0) ? false : true);
		event.setEnd(new Date(this.getLong(this.getColumnIndex(Event.FIELD_END))));
		event.setPhotoCount(this.getLong(this.getColumnIndex(Event.FIELD_PHOTO_COUNT)));
		event.setPin(this.getString(this.getColumnIndex(Event.FIELD_PIN)));
		event.setIsPublic((this.getInt(this.getColumnIndex(Event.FIELD_PUBLIC)) == 0) ? false : true);
		event.setResourceUri(this.getString(this.getColumnIndex(Event.FIELD_RESOURCE_URI)));
		event.setStart(new Date(this.getLong(this.getColumnIndex(Event.FIELD_START))));
		event.setTitle(this.getString(this.getColumnIndex(Event.FIELD_TITLE)));
		event.setUrl(this.getString(this.getColumnIndex(Event.FIELD_URL)));

		return event;
	}

}
