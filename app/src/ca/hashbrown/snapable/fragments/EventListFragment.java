package ca.hashbrown.snapable.fragments;

import com.snapable.api.models.Event;

import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.activities.EventPhotoList;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class EventListFragment extends ListFragment implements LoaderCallbacks<Cursor>, OnItemClickListener, LocationListener {
	
	private static final String TAG = "EventListFragment";
	
	public static final class LOADERS {
		public static final int EVENTS = 0x01;
		public static final int EVENTS_GPS = 0x02;
	}

	EventListAdapter eventAdapter;
	LocationManager locationManager;
	Handler msgHandler;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// initialize/setup some basic stuff
		msgHandler = new Handler();
		getListView().setOnItemClickListener(this);

		eventAdapter = new EventListAdapter(getActivity(), null);
		setListAdapter(eventAdapter);
		
		// Retrieve a list of location providers that have fine accuracy, no monetary cost, etc
    	Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);
    	criteria.setCostAllowed(false);
    	
    	locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    	String providerName = locationManager.getBestProvider(criteria, true);

    	// If no suitable provider is found, null is returned.
    	if (providerName != null) {
    		locationManager.requestLocationUpdates(providerName, 1000, 1, this);
    	}

    	// add a message to kill the location updater if it takes more than 10 sec.
    	class GpsTimeout implements Runnable {
    		
    		private LocationManager locationManager;
			private LocationListener locationListener;

    		public GpsTimeout(LocationManager locationManager, LocationListener locationListener) {
    			this.locationManager = locationManager;
    			this.locationListener = locationListener;
    		}
    		
    		public void run() {
    			Log.d(TAG, "kill the location updates");
				locationManager.removeUpdates(locationListener);
				stopLoadingSpinner(true);
			}
		}
    	msgHandler.postDelayed(new GpsTimeout(locationManager, this), 10000);
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
		stopLoadingSpinner(true);
		
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

		// convert into an event cursor
		EventCursor eventCursor = new EventCursor(c);
		Event event = eventCursor.getEvent();

		// if there are stored, make sure pins match
		if(cachedPinMatchesEventPin(event)) {
			// store the event as data to be passed
			Intent intent = new Intent(getActivity(), EventPhotoList.class);
			intent.putExtra("event", event);
			startActivity(intent);
		} 
		// no stored pin or pins don't match, launch dialog
		else {
			// prepare the event object
			Bundle args = new Bundle(1);
			args.putParcelable("event", event);

			// start the dialog with the event object
			EventAuthFragment login = new EventAuthFragment();
			login.setArguments(args);
			login.show(getFragmentManager(), "login");
		}
	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Log.d(TAG, location.getLatitude() + " | " + location.getLongitude());
		
		Bundle args = new Bundle(2);
		args.putString("lat", String.valueOf(location.getLatitude()));
		args.putString("lng", String.valueOf(location.getLongitude()));
		
		// Prepare the loader. (Re-connect with an existing one, or start a new one.)
		getLoaderManager().restartLoader(LOADERS.EVENTS_GPS, args, this);
		locationManager.removeUpdates(this); // stop updates
		stopLoadingSpinner(true);
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
	
	private boolean cachedPinMatchesEventPin(Event event) {
		Uri requestUri = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, event.getId());
		Cursor result = getActivity().getContentResolver().query(requestUri, null, null, null, null);
		
		// we have a result
		if (result.getCount() > 0 && event.getIsPublic()) {
			return true;
		}
		else if (result.getCount() > 0 && result.moveToFirst()) {
			return result.getString(result.getColumnIndex(SnapableContract.EventCredentials.PIN)).equals(event.getPin());
		}
		
		// there was no result
		return false;
	}
	
	public void stopLoadingSpinner(boolean animate) {
		// get handles on things
		ProgressBar pb = (ProgressBar) getView().findViewById(R.id.fragment_event_list__progressBar);
		LinearLayout listContainer = (LinearLayout) getView().findViewById(R.id.fragment_event_list__list_container);

		// fade in the list/fade out the spinner
		if (animate) {
			pb.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
			listContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
		}
		
		// set the visibilities
		pb.setVisibility(View.GONE);
		listContainer.setVisibility(View.VISIBLE);
	}
	
	public void startLoadingSpinner(boolean animate) {	
		// get handles on things
		ProgressBar pb = (ProgressBar) getView().findViewById(R.id.fragment_event_list__progressBar);
		LinearLayout listContainer = (LinearLayout) getView().findViewById(R.id.fragment_event_list__list_container);

		// fade in the list/fade out the spinner
		if (animate) {
			pb.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
			listContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
		}
		
		// set the visibilities
		pb.setVisibility(View.VISIBLE);
		listContainer.setVisibility(View.GONE);
	}

}
