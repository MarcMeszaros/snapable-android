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
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;

import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.activities.EventPhotoList;
import ca.hashbrown.snapable.adapters.EventListAdapter;
import ca.hashbrown.snapable.cursors.EventCursor;
import ca.hashbrown.snapable.provider.SnapableContract;
import ca.hashbrown.snapable.api.models.Event;
import ca.hashbrown.snapable.ui.widgets.ScrollableSwipeRefreshLayout;

public class EventListFragment extends SnapListFragment implements SearchView.OnQueryTextListener, LoaderCallbacks<Cursor>, OnItemClickListener, LocationListener, SwipeRefreshLayout.OnRefreshListener {

	private static final String TAG = "EventListFragment";

    public static final class LOADERS {
		public static final int EVENTS = 0x01;
	}

	private EventListAdapter eventAdapter;
	private LocationManager locationManager;
    private SwipeRefreshLayout swipeLayout;
	private Handler msgHandler;
	private Bundle lastLatLng;

    private SearchView mSearchView = null;
    private String mSearchQuery = "";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        // initialize/setup some basic stuff
        msgHandler = new Handler();
        getListView().setOnItemClickListener(this);

		eventAdapter = new EventListAdapter(getActivity(), null);
        setListAdapter(eventAdapter);

        // try and restore the saved state
        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString("searchQuery", "");
        }

		// initialize the loader
        Bundle args = new Bundle(1);
        if (mSearchQuery.length() > 0) {
            args.putString("q", mSearchQuery);
        }
		getLoaderManager().initLoader(LOADERS.EVENTS, args, this);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // try and restore the saved state
        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString("searchQuery", "");
        }
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_event_list, container, false);
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // make the list go into "loading"
        setListShownNoAnimation(false);

        // setup pull to refresh
        swipeLayout = (ScrollableSwipeRefreshLayout) view.findViewById(R.id.fragment_event_list);
        swipeLayout.setOnRefreshListener(this);

        ListView list = (ListView) view.findViewById(android.R.id.list);
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    swipeLayout.setEnabled(true);
                else
                    swipeLayout.setEnabled(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (locationManager != null && msgHandler != null) {
            // restart the loader
            Bundle args = new Bundle(1);
            if (mSearchQuery.length() > 0) {
                args.putString("q", mSearchQuery);
            }
            getLoaderManager().initLoader(LOADERS.EVENTS, args, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationManager != null) {
            locationManager.removeUpdates(this); // stop GPS updates
            setListShownNoAnimation(false);
        }
        if (msgHandler != null) {
            msgHandler.removeCallbacksAndMessages(null); // remove all messages in the handler
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("searchQuery", mSearchQuery);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_event_list, menu);
        mSearchView = (SearchView) menu.findItem(R.id.menu__fragment_event_list__search).getActionView();
        mSearchView.setQueryHint("Event Title or URL");
        mSearchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "search: " + query);
        // build the search param
        mSearchQuery = query;
        Bundle args = new Bundle(1);
        args.putString("q", mSearchQuery);

        // get the fragment, and init the new search loader (using the search param)
        getLoaderManager().restartLoader(LOADERS.EVENTS, args, this);

        // clear focus
        if (mSearchView != null) {
            mSearchView.clearFocus();
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "new query: " + newText.isEmpty() + " " + newText);
        mSearchQuery = newText;
        if (newText.isEmpty()) {
            return false;
        } else {
            return false;
        }
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created.
		// First, pick the base URI to use depending on whether we are
		// currently filtering.

        // start the refresh animation
        setRefreshing(true);

        // get the query string if required
        if (args != null && args.containsKey("q")) {
            Log.d(TAG, "CursorLoader: q");
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

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
        eventAdapter.changeCursor(data);
        setRefreshing(false);
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed. We need to make sure we are no
		// longer using it.
        eventAdapter.changeCursor(null);
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
                fragmentReference.setListShown(true);
			}
		}
    	msgHandler.postDelayed(new GpsTimeout(this, locationManager, this), 5000);
	}

    private void setRefreshing(boolean refreshing) {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(refreshing);
        }
        if (refreshing == false) {
            setListShown(true);
        }
    }

    @Override
    public void onRefresh() {
        Bundle args = new Bundle();
        if (mSearchQuery.length() > 0) {
            args.putString("q", mSearchQuery);
        }

        getLoaderManager().restartLoader(LOADERS.EVENTS, args, this);
    }

}
