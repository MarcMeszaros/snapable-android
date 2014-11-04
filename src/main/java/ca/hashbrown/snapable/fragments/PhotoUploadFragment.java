package ca.hashbrown.snapable.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.hashbrown.snapable.R;

public class PhotoUploadFragment extends SnapFragment {

	public PhotoUploadFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_upload, null);
        View layout = view.findViewById(R.id.fragment_photo_upload);
        layout.requestFocus();
        return view;
	}

}
