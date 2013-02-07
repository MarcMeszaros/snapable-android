package ca.hashbrown.snapable;

import com.google.analytics.tracking.android.GoogleAnalytics;

import android.app.Application;
import android.util.Log;

/**
 * Set some defaults for the Android application based on the build type.
 *
 * @author Marc Meszaros <marc@snapable.com>
 */
public class Snapable extends Application {

	private static final String TAG = "Snapable";

	@Override
	public void onCreate() {
		super.onCreate();

		// if we are in release mode
		if(!BuildConfig.DEBUG) {
			Log.i(TAG, "Starting in release mode.");
			// TODO set release stuff
		} else {
			Log.i(TAG, "Starting in debug mode.");

			// set google analytics to be in debug mode
			GoogleAnalytics.getInstance(this).setDebug(true);
		}
	}

}
