package ca.hashbrown.snapable.fragments;


import ca.hashbrown.snapable.EventPhotoList;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.adapters.EventListAdapter;
import ca.hashbrown.snapable.cursors.EventCursor;
import ca.hashbrown.snapable.provider.SnapableContract;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class EventListFragment extends ListFragment implements LoaderCallbacks<Cursor>, OnItemClickListener {
	
	private static final String TAG = "EventListFragment";
	
	private static final int EVENTS = 0x01;
	
	EventListAdapter eventAdapter;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().setOnItemClickListener(this);
		
		eventAdapter = new EventListAdapter(getActivity(), null);
		setListAdapter(eventAdapter);
		
		// Prepare the loader. (Re-connect with an existing one, or start a new one.)
		getLoaderManager().initLoader(EVENTS, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_snap, null);
	}
	
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created.
		// First, pick the base URI to use depending on whether we are
		// currently filtering.
		switch (id) {
		case EVENTS:
			return new CursorLoader(getActivity(), SnapableContract.Event.CONTENT_URI, null, null, null, null);
		
		default:
			return null;
		}
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
		switch (loader.getId()) {
		case EVENTS:
			eventAdapter.changeCursor(data);
			break;
		
		default:
			break;
		}
		
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed. We need to make sure we are no
		// longer using it.
		switch (loader.getId()) {
		case EVENTS:
			eventAdapter.changeCursor(null);
			break;

		default:
			break;
		}
	}

	// click
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {	
		Cursor c = eventAdapter.getCursor();
		c.moveToPosition(position);
		
		EventCursor eventCursor = new EventCursor(c);

		// store the event as data to be passed
		Intent intent = new Intent(getActivity(), EventPhotoList.class);
		intent.putExtra("event", eventCursor.getEvent());
		startActivity(intent);
		
	}

}
