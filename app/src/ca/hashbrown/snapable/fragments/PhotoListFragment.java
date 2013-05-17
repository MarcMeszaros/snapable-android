package ca.hashbrown.snapable.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.adapters.PhotoListAdapter;
import ca.hashbrown.snapable.provider.SnapableContract;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.manuelpeinado.refreshactionitem.ProgressIndicatorType;
import com.manuelpeinado.refreshactionitem.RefreshActionItem;
import com.snapable.api.models.Event;

public class PhotoListFragment extends SherlockListFragment implements LoaderCallbacks<Cursor>, RefreshActionItem.RefreshActionListener {

	private static final String TAG = "PhotoListFragment";

	private static final int PHOTOS = 0x01;

	PhotoListAdapter photoAdapter;
	Event event;
    RefreshActionItem refreshActionItem;

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
        MenuItem item = menu.findItem(R.id.menu__fragment_photo_list__refresh);
        refreshActionItem = (RefreshActionItem) item.getActionView();
        refreshActionItem.setMenuItem(item);
        refreshActionItem.setProgressIndicatorType(ProgressIndicatorType.INDETERMINATE);
        refreshActionItem.setMax(100);
        refreshActionItem.setRefreshActionListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
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
                if (refreshActionItem != null)
                    refreshActionItem.showProgress(true);
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
                photoAdapter.changeCursor(data);
                if (refreshActionItem != null)
                    refreshActionItem.showProgress(false);
                break;

            default:
                break;
		}

	}

	public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset");
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

	public void setEvent(Event event) {
		this.event = event;
		this.getLoaderManager().initLoader(PHOTOS, null, this);
	}

    @Override
    public void onRefreshButtonClick(RefreshActionItem sender) {
        getLoaderManager().restartLoader(PHOTOS, null, this);
    }
}
