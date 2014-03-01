package ca.hashbrown.snapable.fragments;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.*;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.activities.EventPhotoList;
import ca.hashbrown.snapable.adapters.EventListAdapter;
import ca.hashbrown.snapable.cursors.EventCursor;
import ca.hashbrown.snapable.provider.SnapableContract;
import ca.hashbrown.snapable.api.models.Event;

public class EventListFragment extends ListFragment implements LoaderCallbacks<Cursor>, OnItemClickListener, LocationListener {

	private static final String TAG = "EventListFragment";

	public static final class LOADERS {
		public static final int EVENTS = 0x01;
	}

	private EventListAdapter eventAdapter;
	private LocationManager locationManager;
	private Handler msgHandler;
	private Bundle lastLatLng;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// initialize/setup some basic stuff
		msgHandler = new Handler();
		getListView().setOnItemClickListener(this);

		eventAdapter = new EventListAdapter(getActivity(), null);
        setListAdapter(eventAdapter);

		// initialize the loader
		getLoaderManager().initLoader(LOADERS.EVENTS, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_event_list, container, false);
	}

    @Override
    public void onResume() {
        super.onResume();
        if (locationManager != null && msgHandler != null) {
            // restart the loader
            getLoaderManager().initLoader(LOADERS.EVENTS, null, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationManager != null) {
            locationManager.removeUpdates(this); // stop GPS updates
            stopLoadingSpinner(false);
        }
        if (msgHandler != null) {
            msgHandler.removeCallbacksAndMessages(null); // remove all messages in the handler
        }
    }

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created.
		// First, pick the base URI to use depending on whether we are
		// currently filtering.
		switch (id) {
			case LOADERS.EVENTS: {
				// get the query string if required
				if (args != null && args.containsKey("q")) {
					Log.d(TAG, "CursorLoader: q");
					startLoadingSpinner(false);
					String selection = "q=?";
					String[] selectionArgs = {args.getString("q")};
					return new CursorLoader(getActivity(), SnapableContract.Event.CONTENT_URI, null, selection, selectionArgs, null);
				} else if (args != null && args.containsKey("lat") && args.containsKey("lng")) {
					Log.d(TAG, "CursorLoader: lat|lng");
					String selection = "lat=? lng=?";
					String[] selectionArgs = {args.getString("lat"), args.getString("lng")};
					return new CursorLoader(getActivity(), SnapableContract.Event.CONTENT_URI, null, selection, selectionArgs, null);
				} else {
					Log.d(TAG, "CursorLoader: getLatLng()");
					startLoadingSpinner(false);
					if (lastLatLng == null) {
                        getLatLng();
                        return new Loader<Cursor>(getActivity());
                    } else {
                        Log.d(TAG, "CursorLoader: lat|lng");
                        String selection = "lat=? lng=?";
                        String[] selectionArgs = {lastLatLng.getString("lat"), lastLatLng.getString("lng")};
                        return new CursorLoader(getActivity(), SnapableContract.Event.CONTENT_URI, null, selection, selectionArgs, null);
                    }
                }
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
			stopLoadingSpinner(true);
			break;

		default:
			eventAdapter.changeCursor(data);
            stopLoadingSpinner(true);
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
		Log.d(TAG, location.getLatitude() + " | " + location.getLongitude());

		if (lastLatLng == null) {
            lastLatLng = new Bundle(2);
        }
        lastLatLng.putString("lat", String.valueOf(location.getLatitude()));
		lastLatLng.putString("lng", String.valueOf(location.getLongitude()));

		// Prepare the loader. (Re-connect with an existing one, or start a new one.)
		locationManager.removeUpdates(this); // stop updates
		getLoaderManager().restartLoader(LOADERS.EVENTS, lastLatLng, this);
		msgHandler.removeCallbacksAndMessages(null); // remove all messages in the handler
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

        try {
            // we have a result
            if (result.getCount() > 0 && event.is_public) {
                return true;
            }
            else if (result.getCount() > 0 && result.moveToFirst()) {
                return result.getString(result.getColumnIndex(SnapableContract.EventCredentials.PIN)).equals(event.pin);
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer while trying to access cached event PIN", e);
            return false;
        }

		// there was no result
		return false;
	}

    @Override
    public void setListShown(boolean shown) {
        try {
            // get handles on things
            ProgressBar pb = (ProgressBar) getView().findViewById(R.id.fragment_event_list__progressBar);
            LinearLayout listContainer = (LinearLayout) getView().findViewById(R.id.fragment_event_list__list_container);

            if (shown) {
                // set animation
                pb.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                listContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
                // set the visibilities
                pb.setVisibility(View.GONE);
                listContainer.setVisibility(View.VISIBLE);
            } else {
                // set animation
                pb.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
                listContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                // set the visibilities
                pb.setVisibility(View.VISIBLE);
                listContainer.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "we couldn't find the progress bar", e);
        }
    }

    @Override
    public void setListShownNoAnimation(boolean shown) {
        try {
            // get handles on things
            ProgressBar pb = (ProgressBar) getView().findViewById(R.id.fragment_event_list__progressBar);
            LinearLayout listContainer = (LinearLayout) getView().findViewById(R.id.fragment_event_list__list_container);

            if (shown) {
                // set the visibilities
                pb.setVisibility(View.GONE);
                listContainer.setVisibility(View.VISIBLE);
            } else {
                // set the visibilities
                pb.setVisibility(View.VISIBLE);
                listContainer.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "we couldn't find the progress bar", e);
        }
    }

    /**
     * Show the list and hide the loading spinner.
     *
     * @deprecated use {@link #setListShown(boolean)} or {@link #setListShownNoAnimation(boolean)}
     */
    public void stopLoadingSpinner(boolean animate) {
        if (animate) {
		    setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
	}

    /**
     * Hide the list and show the loading spinner.
     *
     * @deprecated use {@link #setListShown(boolean)} or {@link #setListShownNoAnimation(boolean)}
     */
    public void startLoadingSpinner(boolean animate) {
        if (animate) {
            setListShown(false);
        } else {
            setListShownNoAnimation(false);
        }
	}

	private void getLatLng() {
		Log.d(TAG, "getLatLng()");
		// Retrieve a list of location providers that have fine accuracy, no monetary cost, etc
    	Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_COARSE);
    	criteria.setCostAllowed(false);

    	locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    	String providerName = locationManager.getBestProvider(criteria, true);

    	// If no suitable provider is found, null is returned.
    	if (providerName != null) {
    		locationManager.requestLocationUpdates(providerName, 1000, 1, this);
    	}

    	// add a message to kill the location updater if it takes more than 5 sec.
    	class GpsTimeout implements Runnable {

            private EventListFragment fragmentReference;
    		private LocationManager locationManager;
			private LocationListener locationListener;

    		public GpsTimeout(EventListFragment fragmentReference, LocationManager locationManager, LocationListener locationListener) {
    			this.fragmentReference = fragmentReference;
                this.locationManager = locationManager;
    			this.locationListener = locationListener;
    		}

    		public void run() {
    			Log.d(TAG, "kill the location updates");
				locationManager.removeUpdates(locationListener);
                fragmentReference.stopLoadingSpinner(true);
			}
		}
    	msgHandler.postDelayed(new GpsTimeout(this, locationManager, this), 5000);
	}

}
