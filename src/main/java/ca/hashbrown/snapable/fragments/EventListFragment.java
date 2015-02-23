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
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.snapable.api.private_v1.objects.Event;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.activities.EventPhotoList;
import ca.hashbrown.snapable.adapters.EventListAdapter;
import ca.hashbrown.snapable.loaders.EventLoader;
import ca.hashbrown.snapable.loaders.LoaderResponse;
import ca.hashbrown.snapable.provider.SnapableContract;
import timber.log.Timber;

public class EventListFragment extends SnapListFragment implements SearchView.OnQueryTextListener,
        LoaderCallbacks<LoaderResponse<Event>>, LocationListener,
        SwipeRefreshLayout.OnRefreshListener, SnapListFragment.LoadMoreListener {

    public static final int LOADER_EVENTS = "EventLoader".hashCode();

    private static final String ARG_LOADER_QUERY = "arg.loader.query";
    private static final String ARG_LOADER_LAT = "arg.loader.lat";
    private static final String ARG_LOADER_LNG = "arg.loader.lng";

    private static final String STATE_LAST_LOCATION = "state.last.location";
    private static final String STATE_QUERY = "state.query";

	private EventListAdapter mAdapter;
	private LocationManager locationManager;
	private Handler msgHandler;
	private Bundle lastLatLng;

    private SearchView mSearchView = null;
    private String mSearchQuery = "";

    @InjectView(R.id.fragment_event_list)
    android.support.v4.widget.SwipeRefreshLayout mSwipeLayout;

    @InjectView(android.R.id.list)
    ListView mList;

    public static EventListFragment getInstance() {
        return new EventListFragment();
    }

    //==== LifeCycle ====\\
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        msgHandler = new Handler(Looper.myLooper());

        // try and restore the saved state
        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString(STATE_QUERY, "");
            lastLatLng = savedInstanceState.getBundle(STATE_LAST_LOCATION);
        }
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_event_list, container, false);
        ButterKnife.inject(this, v);
        return v;
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setLoadMoreListener(this);

        // make the list go into "loading"
        setListShownNoAnimation(false);

        // setup pull to refresh
        mSwipeLayout.setOnRefreshListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // initialize/setup some basic stuff
        mAdapter = new EventListAdapter(getActivity());
        setListAdapter(mAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = mAdapter.getItem(position);

                // check the event pins
                if (cachedPinMatchesEventPin(event)) {
                    startActivity(EventPhotoList.initIntent(getActivity(), event));
                } else {
                    EventAuthFragment login = EventAuthFragment.getInstance(event);
                    login.show(getFragmentManager(), EventAuthFragment.class.getCanonicalName());
                }
            }
        });

        // initialize the loader
        if (!TextUtils.isEmpty(mSearchQuery)) {
            Bundle args = new Bundle(1);
            args.putString(ARG_LOADER_QUERY, mSearchQuery);
            getLoaderManager().initLoader(LOADER_EVENTS, args, this);
        } else {
            getLatLng(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationManager != null) {
            locationManager.removeUpdates(this); // stop GPS updates
        }
        if (msgHandler != null) {
            msgHandler.removeCallbacksAndMessages(null); // remove all messages in the handler
        }
    }

    //==== State ====\\
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_QUERY, mSearchQuery);
        outState.putBundle(STATE_LAST_LOCATION, lastLatLng);
    }

    //==== Menu ====\\
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_event_list, menu);
        mSearchView = (SearchView) menu.findItem(R.id.menu__fragment_event_list__search).getActionView();
        mSearchView.setQueryHint(getString(R.string.fragment_event_list__search_hint));
        mSearchView.setOnQueryTextListener(this);
    }

    //==== Search ====\\
    @Override
    public boolean onQueryTextSubmit(String query) {
        // build the search param
        mSearchQuery = query;

        // get the fragment, and init the new search loader (using the search param)
        Bundle args = new Bundle(1);
        args.putString(ARG_LOADER_QUERY, mSearchQuery);
        getLoaderManager().restartLoader(LOADER_EVENTS, args, this);
        mSwipeLayout.setRefreshing(true);

        // clear focus
        if (mSearchView != null) {
            mSearchView.clearFocus();
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mSearchQuery = newText;
        if (TextUtils.isEmpty(newText)) {
            return false;
        } else {
            return false;
        }
    }

    //==== Loaders ====\\
    public Loader<LoaderResponse<Event>> onCreateLoader(int id, Bundle args) {
        // get the query string if required
        if (args != null && args.containsKey(ARG_LOADER_QUERY)) {
            return new EventLoader(getActivity(), args.getString(ARG_LOADER_QUERY));
        } else if (args != null && args.containsKey(ARG_LOADER_LAT) && args.containsKey(ARG_LOADER_LNG)) {
            return new EventLoader(getActivity(), args.getFloat(ARG_LOADER_LAT), args.getFloat(ARG_LOADER_LNG));
        } else {
            throw new RuntimeException("This should never happen.");
        }
	}

    public void onLoadFinished(Loader<LoaderResponse<Event>> loader, LoaderResponse<Event> response) {
		// For the first page, clear the data from adapter.
        if(response.type == LoaderResponse.TYPE.FIRST && !response.data.isEmpty())
            mAdapter.clear();

        mAdapter.addAll(response.data);
        if (isResumed()) {
            setListShown(true); // make sure the list is displayed
        } else {
            setListShownNoAnimation(true);
        }
        mSwipeLayout.setRefreshing(false);
	}

	public void onLoaderReset(Loader<LoaderResponse<Event>> loader) {
		mAdapter.clear();
	}

    //==== Location ====\\
	public void onLocationChanged(Location location) {
        startLoader(location);

		// Prepare the loader. (Re-connect with an existing one, or start a new one.)
		locationManager.removeUpdates(this); // stop updates
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

    public void startLoader(Location location) {
        if (lastLatLng == null) {
            lastLatLng = new Bundle(2);
        }
        lastLatLng.putFloat(ARG_LOADER_LAT, (float) location.getLatitude());
        lastLatLng.putFloat(ARG_LOADER_LNG, (float) location.getLongitude());
        getLoaderManager().initLoader(LOADER_EVENTS, lastLatLng, this);
    }

    //==== Helpers ====\\
	private boolean cachedPinMatchesEventPin(Event event) {
		Uri requestUri = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, event.getPk());
		Cursor result = getActivity().getContentResolver().query(requestUri, null, null, null, null);
        boolean isAllowed = false;

        try {
            // we have a result
            if (result.getCount() > 0 && event.isPublic) {
                isAllowed = true;
            } else if (result.getCount() > 0 && result.moveToFirst()) {
                isAllowed = result.getString(result.getColumnIndex(SnapableContract.EventCredentials.PIN)).equals(event.pin);
            }
        } catch (NullPointerException e) {
            Timber.e(e, "Null pointer while trying to access cached event PIN");
            isAllowed = false;
        } finally {
            result.close();
        }

		// there was no result
		return isAllowed;
	}

	private void getLatLng(LocationListener locationListener) {
        // Retrieve a list of location providers that have fine accuracy, no monetary cost, etc
    	Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_COARSE);
    	criteria.setCostAllowed(false);

        // get location
    	locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    	String providerName = locationManager.getBestProvider(criteria, true);

    	// If no suitable provider is found, null is returned.
    	if (!TextUtils.isEmpty(providerName)) {
            Location location = locationManager.getLastKnownLocation(providerName);
    		locationManager.requestLocationUpdates(providerName, 1000, 1, this);
            if (location != null) {
                startLoader(location);
            }
    	}

    	// add a message to kill the location updater if it takes more than 5 sec.
    	msgHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                locationManager.removeUpdates(EventListFragment.this);
                setListShown(true);
                mSwipeLayout.setRefreshing(false);
            }
        }, 5000);
	}

    @Override
    public void onRefresh() {
        if (!TextUtils.isEmpty(mSearchQuery)) {
            Bundle args = new Bundle();
            args.putString(ARG_LOADER_QUERY, mSearchQuery);
            getLoaderManager().restartLoader(LOADER_EVENTS, args, this);
        } else {
            getLatLng(this);
        }
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
