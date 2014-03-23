package ca.hashbrown.snapable.fragments;

import android.app.Fragment;

import com.octo.android.robospice.SpiceManager;

import ca.hashbrown.snapable.api.robospice.SnapSpiceService;

public abstract class SnapFragment extends Fragment {

    protected SpiceManager apiRequestManager = new SpiceManager(SnapSpiceService.class);

    @Override
    public void onStart() {
        super.onStart();
        apiRequestManager.start(getActivity());
    }

    @Override
    public void onStop() {
        apiRequestManager.shouldStop();
        super.onStop();
    }

}
