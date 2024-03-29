package ca.hashbrown.snapable.fragments;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.view.*;

import com.snapable.api.private_v1.objects.Event;
import com.snapable.api.private_v1.objects.Photo;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.activities.PhotoUpload;
import ca.hashbrown.snapable.adapters.BaseRecyclerAdapter;
import ca.hashbrown.snapable.adapters.PhotoRecyclerAdapter;
import ca.hashbrown.snapable.loaders.LoaderResponse;
import ca.hashbrown.snapable.loaders.PhotoLoader;
import ca.hashbrown.snapable.ui.widgets.EmptyRecyclerView;

public class PhotoListFragment extends SnapListFragment implements SwipeRefreshLayout.OnRefreshListener, LoaderCallbacks<LoaderResponse<Photo>> {

	public static final int ACTION_GALLERY = 0x02;

    private static final String ARG_EVENT = "arg.event";
    private static final String ARG_LOADER_EVENT_ID = "arg.loader.event.id";
    private static final String ARG_LOADER_EVENT_IS_STREAMABLE = "arg.loader.event.streamable";
    private static final String STATE_EVENT = "state.event";

    private static final int LOADER_PHOTOS = "PhotoLoader".hashCode();

	private PhotoRecyclerAdapter mAdapter;
	private Event mEvent;

    @InjectView(R.id.fragment_photo_list)
    SwipeRefreshLayout mSwipeLayout;

    @InjectView(android.R.id.list)
    EmptyRecyclerView mRecyclerView;

    public static PhotoListFragment getInstance(Event event) {
        PhotoListFragment photoListFragment = new PhotoListFragment();
        Bundle args = new Bundle(1);
        args.putSerializable(ARG_EVENT, event);
        photoListFragment.setArguments(args);
        return photoListFragment;
    }

    //==== LifeCycle ====\\
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mEvent = (Event) getArguments().getSerializable(ARG_EVENT);
        } else if (savedInstanceState != null) {
            mEvent = (Event) savedInstanceState.getSerializable(STATE_EVENT);
        } else {
            throw new RuntimeException("Should always have an event");
        }
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_photo_list, null);
        ButterKnife.inject(this, v);
        return v;
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // add some animation
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // use a linear layout manager
        final int numColumns = getResources().getInteger(R.integer.fragment_photo_list__columns);
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), numColumns);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setOnScrollListener(new BaseRecyclerAdapter.EndlessRecyclerOnScrollListener(layoutManager){
            @Override
            public void onLoadMore(int currentPage) {
                Loader<LoaderResponse<Photo>> loader = getLoaderManager().getLoader(LOADER_PHOTOS);
                if (loader != null && !((PhotoLoader) loader).isProcessing() && ((PhotoLoader) loader).hasNextPage()) {
                    ((PhotoLoader) loader).loadNextPage();
                }
            }
        });

        // make the list go into "loading"
        setListShownNoAnimation(false);

        // setup pull to refresh
        mSwipeLayout.setOnRefreshListener(this);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // setup the adapter
        mAdapter = new PhotoRecyclerAdapter(getActivity());
        setRecyclerAdapter(mAdapter);

        // Prepare the loader. (Re-connect with an existing one, or start a new one.)
        Bundle args = new Bundle(2);
        args.putLong(ARG_LOADER_EVENT_ID, mEvent.getPk());
        args.putBoolean(ARG_LOADER_EVENT_IS_STREAMABLE, true);
        getLoaderManager().initLoader(LOADER_PHOTOS, args, this);
    }

    //==== State ====\\
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_EVENT, mEvent);
    }

    //==== Menu ====\\
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_photo_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu__fragment_photo_list__upload:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/jpeg");
                startActivityForResult(intent, ACTION_GALLERY);
                return true;

            case R.id.menu__fragment_photo_list__credentials:
                EventAuthFragment login = EventAuthFragment.getInstance(mEvent, true);
                login.show(getFragmentManager(), EventAuthFragment.class.getCanonicalName());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Cursor cursor = getActivity().getContentResolver().query(data.getData(), null, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String fileSrc = cursor.getString(idx);
            cursor.close();

            // pass all the data to the photo upload activity
            startActivity(PhotoUpload.initIntent(getActivity(), mEvent, fileSrc));
        }
    }

    public Loader<LoaderResponse<Photo>> onCreateLoader(int id, Bundle args) {
        if (args != null) {
            return new PhotoLoader(getActivity(), args.getLong(ARG_LOADER_EVENT_ID), args.getBoolean(ARG_LOADER_EVENT_IS_STREAMABLE));
        } else {
            return null;
        }
	}

	public void onLoadFinished(Loader<LoaderResponse<Photo>> loader, LoaderResponse<Photo> response) {
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

	public void onLoaderReset(Loader<LoaderResponse<Photo>> loader) {
        mAdapter.clear();
	}

    @Override
    public void onRefresh() {
        Bundle args = new Bundle(2);
        args.putLong(ARG_LOADER_EVENT_ID, mEvent.getPk());
        args.putBoolean(ARG_LOADER_EVENT_IS_STREAMABLE, true);
        getLoaderManager().restartLoader(LOADER_PHOTOS, args, this);
    }

}
