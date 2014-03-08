package ca.hashbrown.snapable.activities;

import android.app.Activity;
import android.os.Bundle;
import ca.hashbrown.snapable.BuildConfig;
import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.EasyTracker;

public abstract class BaseFragmentActivity extends Activity {

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
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }
}