package ca.hashbrown.snapable.activities;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;

public abstract class BaseActivity extends Activity {

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        EasyTracker.getInstance(this).activityStop(this);
        super.onStop();
    }
}
