package ca.hashbrown.snapable.fragments;

import ca.hashbrown.snapable.EventPhotoList;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.adapters.EventListAdapter;
import ca.hashbrown.snapable.cursors.EventCursor;
import ca.hashbrown.snapable.provider.SnapableContract;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class EventListFragment extends ListFragment implements LoaderCallbacks<Cursor>, OnItemClickListener, LocationListener {
	
	private static final String TAG = "EventListFragment";
	
	public static final class LOADERS {
		public static final int EVENTS = 0x01;
		public static final int EVENTS_GPS = 0x02;
	}

	EventListAdapter eventAdapter;
	LocationManager locationManager;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().setOnItemClickListener(this);
		
		eventAdapter = new EventListAdapter(getActivity(), null);
		setListAdapter(eventAdapter);
		
		/*
		// Retrieve a list of location providers that have fine accuracy, no monetary cost, etc
    	Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);
    	criteria.setCostAllowed(false);
    	
    	locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    	String providerName = locationManager.getBestProvider(criteria, true);

    	// If no suitable provider is found, null is returned.
    	//LocationProvider provider = null;
    	if (providerName != null) {
    	  // provider = locationManager.getProvider(providerName);
    	   locationManager.requestLocationUpdates(providerName, 1000, 1, this);
    	}
    	*/
		
		// Prepare the loader. (Re-connect with an existing one, or start a new one.)
    	Bundle args = new Bundle(2);
		args.putString("lat", "45.427324");
		args.putString("lng", "-75.691542");
		getLoaderManager().initLoader(LOADERS.EVENTS_GPS, args, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_event_list, container, false);
	}
	
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created.
		// First, pick the base URI to use depending on whether we are
		// currently filtering.
		switch (id) {
			case LOADERS.EVENTS: {
				// get the query string if required
				if (args.containsKey("q")) {
					String selection = "q=?";
					String[] selectionArgs = {args.getString("q")};
					return new CursorLoader(getActivity(), SnapableContract.Event.CONTENT_URI, null, selection, selectionArgs, null);
				} else {
					return new CursorLoader(getActivity(), SnapableContract.Event.CONTENT_URI, null, null, null, null);
				}
			}
			case LOADERS.EVENTS_GPS: {
				String selection = "lat=? lng=?";
				String[] selectionArgs = {args.getString("lat"), args.getString("lng")};
				return new CursorLoader(getActivity(), SnapableContract.Event.CONTENT_URI, null, selection, selectionArgs, null);
			}
			default: {
				return null;
			}
		}
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
		switch (loader.getId()) {
		case LOADERS.EVENTS:
			eventAdapter.changeCursor(data);
			break;
		case LOADERS.EVENTS_GPS:
			eventAdapter.changeCursor(data);
			break;
		
		default:
			eventAdapter.changeCursor(data);
			break;
		}
		
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed. We need to make sure we are no
		// longer using it.
		switch (loader.getId()) {
		case LOADERS.EVENTS:
			eventAdapter.changeCursor(null);
			break;
		case LOADERS.EVENTS_GPS:
			eventAdapter.changeCursor(null);
			break;

		default:
			eventAdapter.changeCursor(null);
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

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Log.d(TAG, location.getLatitude() + " | " + location.getLongitude());
		
		Bundle args = new Bundle(2);
		args.putString("lat", String.valueOf(location.getLatitude()));
		args.putString("lng", String.valueOf(location.getLongitude()));
		
		// Prepare the loader. (Re-connect with an existing one, or start a new one.)
		getLoaderManager().initLoader(LOADERS.EVENTS_GPS, args, this);
		locationManager.removeUpdates(this); // stop updates
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
