package ca.hashbrown.snapable.activities;

import ca.hashbrown.snapable.api.robospice.SnapSpiceService;

import com.google.analytics.tracking.android.EasyTracker;
import com.octo.android.robospice.SpiceManager;

import android.app.Activity;

public abstract class BaseActivity extends Activity {

    protected SpiceManager apiRequestManager = new SpiceManager(SnapSpiceService.class);

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
        apiRequestManager.start(this);
    }

    @Override
    protected void onStop() {
        apiRequestManager.shouldStop();
        EasyTracker.getInstance(this).activityStop(this);
        super.onStop();
    }
}
