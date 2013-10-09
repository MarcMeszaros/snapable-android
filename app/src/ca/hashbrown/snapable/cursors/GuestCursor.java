package ca.hashbrown.snapable.cursors;

import com.snapable.api.models.Guest;

import android.database.Cursor;
import android.database.MatrixCursor;

public class GuestCursor extends MatrixCursor {

	/**
	 * Create a new GuestCursor.
	 */
	public GuestCursor() {
		super(Guest.COLUMN_NAMES);
	}

	/**
	 * Create a new GuestCursor.
	 *
	 * @param columnNames The list of columns for the cursor.
	 */
	public GuestCursor(String[] columnNames) {
		super(Guest.COLUMN_NAMES);
	}

	/**
	 * Create a new GuestCursor using a regular Cursor.
	 *
	 * @param c The cursor to convert into an GuestCursor
	 */
	public GuestCursor(Cursor c) {
		super(c.getColumnNames());
		int position = c.getPosition();

		// move to the start position and iterate through the cursor
		c.moveToPosition(-1);
		while (c.moveToNext()) {
			MatrixCursor.RowBuilder row = this.newRow();

			// add the values for the row
			for (String column : c.getColumnNames()) {
				if (column.equals(Guest.FIELD_ID)) { row.add(c.getLong(c.getColumnIndex(Guest.FIELD_ID))); }
				if (column.equals(Guest.FIELD_EMAIL)) { row.add(c.getString(c.getColumnIndex(Guest.FIELD_EMAIL))); }
				if (column.equals(Guest.FIELD_EVENT_URI)) { row.add(c.getString(c.getColumnIndex(Guest.FIELD_EVENT_URI))); }
				if (column.equals(Guest.FIELD_NAME)) { row.add(c.getString(c.getColumnIndex(Guest.FIELD_NAME))); }
				if (column.equals(Guest.FIELD_RESOURCE_URI)) { row.add(c.getString(c.getColumnIndex(Guest.FIELD_RESOURCE_URI))); }
			}
		}

		// move to the position the original cursor was at
		this.moveToPosition(position);
	}

	/**
	 * Add a Guest object to the Cursor.
	 *
	 * @param guest The guest to add to the cursor.
	 */
	public void add(Guest guest) {
		// create a new row
		MatrixCursor.RowBuilder row = this.newRow();

		// add the values for the row
		for (String column : getColumnNames()) {
			if (column.equals(Guest.FIELD_ID)) { row.add(guest.getId()); }
			if (column.equals(Guest.FIELD_EMAIL)) { row.add(guest.email); }
			if (column.equals(Guest.FIELD_EVENT_URI)) { row.add(guest.event_uri); }
			if (column.equals(Guest.FIELD_NAME)) { row.add(guest.name); }
			if (column.equals(Guest.FIELD_RESOURCE_URI)) { row.add(guest.resource_uri); }
		}

	}

	/**
	 * Get a guest object at the position the cursor is currently pointing.
	 *
	 * @return a Guest object at the current cursor position
	 */
	public Guest getGuest() {
		checkPosition();
		Guest guest = new Guest();

		// populate the object
		guest.email = this.getString(this.getColumnIndex(Guest.FIELD_EMAIL));
		guest.event_uri = this.getString(this.getColumnIndex(Guest.FIELD_EVENT_URI));
		guest.name = this.getString(this.getColumnIndex(Guest.FIELD_NAME));
		guest.resource_uri = this.getString(this.getColumnIndex(Guest.FIELD_RESOURCE_URI));

		return guest;
	}

}
