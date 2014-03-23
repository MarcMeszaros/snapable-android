package ca.hashbrown.snapable.activities;

import ca.hashbrown.snapable.BuildConfig;
import ca.hashbrown.snapable.api.robospice.SnapSpiceService;

import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.EasyTracker;
import com.octo.android.robospice.SpiceManager;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {

    protected SpiceManager apiRequestManager = new SpiceManager(SnapSpiceService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.DEBUG) {
            Crashlytics.start(this);
        }
    }

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
