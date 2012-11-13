package ca.hashbrown.snapable.fragments;

import com.snapable.api.models.Event;

import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.adapters.PhotoListAdapter;
import ca.hashbrown.snapable.provider.SnapableContract;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PhotoListFragment extends ListFragment implements LoaderCallbacks<Cursor> {
	
	private static final String TAG = "PhotoListFragment";
	
	private static final int PHOTOS = 0x01;
	
	PhotoListAdapter photoAdapter;
	Event event;
	
	public PhotoListFragment(Event event) {
		this.event = event;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		photoAdapter = new PhotoListAdapter(getActivity(), null);
		setListAdapter(photoAdapter);
		
		// Prepare the loader. (Re-connect with an existing one, or start a new one.)
		getLoaderManager().initLoader(PHOTOS, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_event_list, null);
	}
	
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created.
		// First, pick the base URI to use depending on whether we are
		// currently filtering.
		switch (id) {
		case PHOTOS:
			String selection = "event=?";
			String[] selectionArgs = {String.valueOf(event.getId())};
			return new CursorLoader(getActivity(), SnapableContract.Photo.CONTENT_URI, null, selection, selectionArgs, null);
		
		default:
			return null;
		}
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
		switch (loader.getId()) {
		case PHOTOS:
			photoAdapter.changeCursor(data);
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
		case PHOTOS:
			photoAdapter.changeCursor(null);
			break;

		default:
			break;
		}
	}

}
