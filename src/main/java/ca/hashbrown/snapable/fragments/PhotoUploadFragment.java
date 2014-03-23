package ca.hashbrown.snapable.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.api.models.Event;

public class PhotoUploadFragment extends SnapFragment {

	private static final String TAG = "PhotoUploadFragment";

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
