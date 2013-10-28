package ca.hashbrown.snapable.cursors;

import android.database.Cursor;
import android.database.MatrixCursor;
import com.snapable.api.models.Event;

import java.util.Date;

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
	 * @param c The cursor to convert into an EventCursor
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
			if (column.equals(Event.FIELD_ENABLED)) { row.add((event.enabled) ? 1 : 0); }
			if (column.equals(Event.FIELD_END)) { row.add(event.end.getTime()); }
			if (column.equals(Event.FIELD_PHOTO_COUNT)) { row.add(event.photo_count); }
			if (column.equals(Event.FIELD_PIN)) { row.add(event.pin); }
			if (column.equals(Event.FIELD_PUBLIC)) { row.add((event.is_public) ? 1 : 0); }
			if (column.equals(Event.FIELD_RESOURCE_URI)) { row.add(event.resource_uri); }
			if (column.equals(Event.FIELD_START)) { row.add(event.start.getTime()); }
			if (column.equals(Event.FIELD_TITLE)) { row.add(event.title); }
			if (column.equals(Event.FIELD_URL)) { row.add(event.url); }
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
		event.enabled = (this.getInt(this.getColumnIndex(Event.FIELD_ENABLED)) == 0) ? false : true;
		event.end = new Date(this.getLong(this.getColumnIndex(Event.FIELD_END)));
		event.photo_count = this.getLong(this.getColumnIndex(Event.FIELD_PHOTO_COUNT));
		event.pin = this.getString(this.getColumnIndex(Event.FIELD_PIN));
		event.is_public = (this.getInt(this.getColumnIndex(Event.FIELD_PUBLIC)) == 0) ? false : true;
		event.resource_uri = this.getString(this.getColumnIndex(Event.FIELD_RESOURCE_URI));
		event.start = new Date(this.getLong(this.getColumnIndex(Event.FIELD_START)));
		event.title = this.getString(this.getColumnIndex(Event.FIELD_TITLE));
		event.url = this.getString(this.getColumnIndex(Event.FIELD_URL));

		return event;
	}

}
