package ca.hashbrown.snapable.fragments;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView;

import com.snapable.api.private_v1.objects.Event;

import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.activities.EventPhotoList;
import ca.hashbrown.snapable.adapters.EventListAdapter;
import ca.hashbrown.snapable.loaders.EventLoader;
import ca.hashbrown.snapable.loaders.LoaderResponse;
import ca.hashbrown.snapable.provider.SnapableContract;
import ca.hashbrown.snapable.ui.widgets.ScrollableSwipeRefreshLayout;
import timber.log.Timber;

public class EventListFragment extends SnapListFragment implements SearchView.OnQueryTextListener,
        LoaderCallbacks<LoaderResponse<Event>>, OnItemClickListener, LocationListener,
        SwipeRefreshLayout.OnRefreshListener, SnapListFragment.LoadMoreListener {

    public static final int LOADER_EVENTS = "EventLoader".hashCode();

    private static final String ARG_LOADER_QUERY = "arg.loader.query";
    private static final String ARG_LOADER_LAT = "arg.loader.lat";
    private static final String ARG_LOADER_LNG = "arg.loader.lng";

    private static final String STATE_QUERY = "state.query";

	private EventListAdapter mAdapter;
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

		mAdapter = new EventListAdapter(getActivity());
        setListAdapter(mAdapter);

        // try and restore the saved state
        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString(STATE_QUERY, "");
        }

		// initialize the loader
        Bundle args = new Bundle(1);
        if (mSearchQuery.length() > 0) {
            args.putString(ARG_LOADER_QUERY, mSearchQuery);
        }
		getLoaderManager().initLoader(LOADER_EVENTS, args, this);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // try and restore the saved state
        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString(STATE_QUERY, "");
        }
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_event_list, container, false);
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setLoadMoreListener(this);

        // make the list go into "loading"
        setListShownNoAnimation(false);

        // setup pull to refresh
        swipeLayout = (ScrollableSwipeRefreshLayout) view.findViewById(R.id.fragment_event_list);
        swipeLayout.setOnRefreshListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (locationManager != null && msgHandler != null) {
            // restart the loader
            Bundle args = new Bundle(1);
            if (mSearchQuery.length() > 0) {
                args.putString(ARG_LOADER_QUERY, mSearchQuery);
            }
            getLoaderManager().initLoader(LOADER_EVENTS, args, this);
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
        outState.putString(STATE_QUERY, mSearchQuery);
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
        Timber.d("search: " + query);
        // build the search param
        mSearchQuery = query;
        Bundle args = new Bundle(1);
        args.putString(ARG_LOADER_QUERY, mSearchQuery);

        // get the fragment, and init the new search loader (using the search param)
        getLoaderManager().restartLoader(LOADER_EVENTS, args, this);

        // clear focus
        if (mSearchView != null) {
            mSearchView.clearFocus();
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Timber.d("new query: " + newText);
        mSearchQuery = newText;
        if (newText.isEmpty()) {
            return false;
        } else {
            return false;
        }
    }

    public Loader<LoaderResponse<Event>> onCreateLoader(int id, Bundle args) {
        // start the refresh animation
        setRefreshing(true);

        // get the query string if required
        if (args != null && args.containsKey(ARG_LOADER_QUERY)) {
            return new EventLoader(getActivity(), args.getString(ARG_LOADER_QUERY));
        } else if (args != null && args.containsKey(ARG_LOADER_LAT) && args.containsKey(ARG_LOADER_LNG)) {
            return new EventLoader(getActivity(), args.getFloat(ARG_LOADER_LAT), args.getFloat(ARG_LOADER_LNG));
        } else {
            if (lastLatLng == null) {
                getLatLng();
                return new EventLoader(getActivity(), "");
            } else {
                return new EventLoader(getActivity(), args.getFloat(ARG_LOADER_LAT), args.getFloat(ARG_LOADER_LNG));
            }
        }
	}

    public void onLoadFinished(Loader<LoaderResponse<Event>> loader, LoaderResponse<Event> response) {
		setListShown(true);
        // For the first page, clear the data from adapter.
        if(response.type == LoaderResponse.TYPE.FIRST)
            mAdapter.clear();

        mAdapter.addAll(response.data);
        if (isResumed()) {
            setListShown(true); // make sure the list is displayed
        } else {
            setListShownNoAnimation(true);
        }
	}

	public void onLoaderReset(Loader<LoaderResponse<Event>> loader) {
		mAdapter.clear();
	}

	// click
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Event event = mAdapter.getItem(position);
        // if there are stored, make sure pins match
		if(cachedPinMatchesEventPin(event)) {
			// store the event as data to be passed
			startActivity(EventPhotoList.initIntent(getActivity(), event));
		}
		// no stored pin or pins don't match, launch dialog
		else {
			// start the dialog with the event object
			EventAuthFragment login = EventAuthFragment.getInstance(event);
			login.show(getFragmentManager(), EventAuthFragment.class.getCanonicalName());
		}
	}

	public void onLocationChanged(Location location) {
        if (lastLatLng == null) {
            lastLatLng = new Bundle(2);
        }
        lastLatLng.putFloat(ARG_LOADER_LAT, (float) location.getLatitude());
		lastLatLng.putFloat(ARG_LOADER_LNG, (float) location.getLongitude());

		// Prepare the loader. (Re-connect with an existing one, or start a new one.)
		locationManager.removeUpdates(this); // stop updates
		getLoaderManager().restartLoader(LOADER_EVENTS, lastLatLng, this);
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
		Uri requestUri = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, event.getPk());
		Cursor result = getActivity().getContentResolver().query(requestUri, null, null, null, null);

        try {
            // we have a result
            if (result.getCount() > 0 && event.is_public) {
                return true;
            } else if (result.getCount() > 0 && result.moveToFirst()) {
                return result.getString(result.getColumnIndex(SnapableContract.EventCredentials.PIN)).equals(event.pin);
            }
        } catch (NullPointerException e) {
            Timber.e(e, "Null pointer while trying to access cached event PIN");
            return false;
        }

		// there was no result
		return false;
	}

	private void getLatLng() {
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
                Timber.d("kill the location updates");
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
            args.putString(ARG_LOADER_QUERY, mSearchQuery);
        }

        getLoaderManager().restartLoader(LOADER_EVENTS, args, this);
    }

    //==== SnapListFragment.LoadMoreListener ====\\
    @Override
    public void loadMore() {
        Loader<LoaderResponse<Event>> loader = getLoaderManager().getLoader(LOADER_EVENTS);
        if (loader != null && !((EventLoader) loader).isProcessing() && ((EventLoader) loader).hasNextPage()) {
            ((EventLoader) loader).loadNextPage();
        }
    }

}
