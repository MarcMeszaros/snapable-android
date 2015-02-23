package ca.hashbrown.snapable.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import ca.hashbrown.snapable.R;

public class PhotoUploadFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_upload, null, false);
        ButterKnife.inject(this, view);
        View layout = view.findViewById(R.id.fragment_photo_upload);
        layout.requestFocus();
        return view;
	}

}
