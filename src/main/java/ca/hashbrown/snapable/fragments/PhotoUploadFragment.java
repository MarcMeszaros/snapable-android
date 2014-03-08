package ca.hashbrown.snapable.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.api.models.Event;

public class PhotoUploadFragment extends Fragment {

	private static final String TAG = "PhotoUploadFragment";

	public PhotoUploadFragment() {
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
