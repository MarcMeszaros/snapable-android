package ca.hashbrown.snapable.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ca.hashbrown.snapable.R;
import com.actionbarsherlock.app.SherlockFragment;
import com.snapable.api.models.Event;

public class PhotoUploadFragment extends SherlockFragment {

	private static final String TAG = "PhotoUploadFragment";

	Event event;

	public PhotoUploadFragment() {
	}

	public PhotoUploadFragment(Event event) {
		this.event = event;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_photo_upload, null);
	}

}
