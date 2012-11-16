package ca.hashbrown.snapable.fragments;

import com.snapable.api.models.Event;

import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.adapters.PhotoListAdapter;
import ca.hashbrown.snapable.provider.SnapableContract;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PhotoShareFragment extends Fragment {
	
	private static final String TAG = "PhotoShareFragment";
	
	Event event;
	
	public PhotoShareFragment() {
	}
	
	public PhotoShareFragment(Event event) {
		this.event = event;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_photo_share, null);
	}

}
