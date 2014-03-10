package ca.hashbrown.snapable.fragments;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;

import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.activities.PhotoUpload;
import ca.hashbrown.snapable.adapters.PhotoListAdapter;
import ca.hashbrown.snapable.provider.SnapableContract;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import ca.hashbrown.snapable.api.models.Event;

public class PhotoListFragment extends SnapListFragment implements OnRefreshListener, LoaderCallbacks<Cursor> {

	private static final String TAG = "PhotoListFragment";

	private static final int PHOTOS = 0x01;
    public static final int GALLERY_ACTION = 0x02;

	PhotoListAdapter photoAdapter;
	Event event;
    private PullToRefreshLayout mPullToRefreshLayout;

    // never used, but we need it to compile
	public PhotoListFragment() {
    }

	public PhotoListFragment(Event event) {
        this.event = event;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        setListShownNoAnimation(false);
		photoAdapter = new PhotoListAdapter(getActivity(), null);
        setListAdapter(photoAdapter);

		// Prepare the loader. (Re-connect with an existing one, or start a new one.)
		getLoaderManager().initLoader(PHOTOS, null, this);
	}

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_photo_list, null);
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // This is the View which is created by ListFragment
        ViewGroup viewGroup = (ViewGroup) view;

        // We need to create a PullToRefreshLayout manually
        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup) // We need to insert the PullToRefreshLayout into the Fragment's ViewGroup
                // We need to mark the ListView and it's Empty View as pullable
                // because they are not direct children of the ViewGroup
                .theseChildrenArePullable(getListView(), getListView().getEmptyView())
                .listener(this)
                .setup(mPullToRefreshLayout); // Finally commit the setup to our PullToRefreshLayout
    }

	@Override
	public void onResume() {
		super.onResume();

		// if the loader is already started, reload it
		if(getLoaderManager().getLoader(PHOTOS).isStarted()) {
			getLoaderManager().getLoader(PHOTOS).forceLoad();
		}
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
                startActivityForResult(intent, GALLERY_ACTION);
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
            Intent upload = new Intent(getActivity(), PhotoUpload.class);
            upload.putExtra("event", event);
            upload.putExtra("imagePath", fileSrc);
            startActivity(upload);
        } else {
            // the unhandled result calls the super (and passes it down to fragments)
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");
        // This is called when a new Loader needs to be created.
		// First, pick the base URI to use depending on whether we are
		// currently filtering.
		switch (id) {
		case PHOTOS:
			if (this.event != null) {
                String selection = "event=?";
				String[] selectionArgs = {String.valueOf(event.getId())};
				return new CursorLoader(getActivity(), SnapableContract.Photo.CONTENT_URI, null, selection, selectionArgs, null);
			} else {
				return null;
			}

		default:
			return null;
		}
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished");
		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
		switch (loader.getId()) {
            case PHOTOS:
                photoAdapter.swapCursor(data);
                break;

            default:
                break;
		}
        mPullToRefreshLayout.setRefreshComplete();
        setListShown(true);
	}

	public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset");
        // This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed. We need to make sure we are no
		// longer using it.
		switch (loader.getId()) {
            case PHOTOS:
                photoAdapter.swapCursor(null);
                break;

            default:
                break;
		}
	}

	public void setEvent(Event event) {
		this.event = event;
		this.getLoaderManager().initLoader(PHOTOS, null, this);
	}

    @Override
    public void onRefreshStarted(View view) {
        getLoaderManager().restartLoader(PHOTOS, null, this);
    }

}
