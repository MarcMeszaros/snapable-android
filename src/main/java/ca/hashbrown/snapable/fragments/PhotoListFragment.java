package ca.hashbrown.snapable.fragments;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;

import com.snapable.api.private_v1.objects.Event;
import com.snapable.api.private_v1.objects.Photo;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.activities.PhotoUpload;
import ca.hashbrown.snapable.adapters.PhotoListAdapter;
import ca.hashbrown.snapable.loaders.LoaderResponse;
import ca.hashbrown.snapable.loaders.PhotoLoader;

public class PhotoListFragment extends SnapListFragment implements SwipeRefreshLayout.OnRefreshListener,
        LoaderCallbacks<LoaderResponse<Photo>>, SnapListFragment.LoadMoreListener {

	public static final int ACTION_GALLERY = 0x02;

    private static final String ARG_EVENT = "arg.event";
    private static final String ARG_LOADER_EVENT_ID = "arg.loader.event.id";
    private static final String ARG_LOADER_EVENT_IS_STREAMABLE = "arg.loader.event.streamable";

    private static final int LOADER_PHOTOS = "PhotoLoader".hashCode();

	private PhotoListAdapter mAdapter;
	private Event mEvent;

    @InjectView(R.id.fragment_photo_list)
    SwipeRefreshLayout mSwipeLayout;

    public static PhotoListFragment getInstance(Event event) {
        PhotoListFragment photoListFragment = new PhotoListFragment();
        Bundle args = new Bundle(1);
        args.putSerializable(ARG_EVENT, event);
        photoListFragment.setArguments(args);
        return photoListFragment;
    }

    // never used, but we need it to compile
	public PhotoListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mEvent = (Event) getArguments().getSerializable(ARG_EVENT);
        }
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        setListShownNoAnimation(false);
		mAdapter = new PhotoListAdapter(getActivity());
        setListAdapter(mAdapter);

		// Prepare the loader. (Re-connect with an existing one, or start a new one.)
        Bundle args = new Bundle(2);
        args.putLong(ARG_LOADER_EVENT_ID, mEvent.getPk());
        args.putBoolean(ARG_LOADER_EVENT_IS_STREAMABLE, true);
		getLoaderManager().initLoader(LOADER_PHOTOS, args, this);
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

        setLoadMoreListener(this);

        // make the list go into "loading"
        setListShownNoAnimation(false);

        // setup pull to refresh
        mSwipeLayout.setOnRefreshListener(this);
    }

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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Cursor cursor = getActivity().getContentResolver().query(data.getData(), null, null, null, null);
            cursor.moveToFirst();  //if not doing this, 01-22 19:17:04.564: ERROR/AndroidRuntime(26264): Caused by: android.database.CursorIndexOutOfBoundsException: Index -1 requested, with a size of 1
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String fileSrc = cursor.getString(idx);

            // pass all the data to the photo upload activity
            startActivity(PhotoUpload.initIntent(getActivity(), mEvent, fileSrc));
        } else {
            // the unhandled result calls the super (and passes it down to fragments)
            super.onActivityResult(requestCode, resultCode, data);
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

	public void onLoaderReset(Loader<LoaderResponse<Photo>> loader) {
        mAdapter.clear();
	}

	public void setEvent(Event event) {
		mEvent = event;
        Bundle args = new Bundle(2);
        args.putLong(ARG_LOADER_EVENT_ID, mEvent.getPk());
        args.putBoolean(ARG_LOADER_EVENT_IS_STREAMABLE, true);
        getLoaderManager().restartLoader(LOADER_PHOTOS, args, this);
	}

    @Override
    public void onRefresh() {
        Bundle args = new Bundle(2);
        args.putLong(ARG_LOADER_EVENT_ID, mEvent.getPk());
        args.putBoolean(ARG_LOADER_EVENT_IS_STREAMABLE, true);
        getLoaderManager().restartLoader(LOADER_PHOTOS, args, this);
    }

    //==== SnapListFragment.LoadMoreListener ====\\
    @Override
    public void loadMore() {
        Loader<LoaderResponse<Photo>> loader = getLoaderManager().getLoader(LOADER_PHOTOS);
        if (loader != null && !((PhotoLoader) loader).isProcessing() && ((PhotoLoader) loader).hasNextPage()) {
            ((PhotoLoader) loader).loadNextPage();
        }
    }

}
