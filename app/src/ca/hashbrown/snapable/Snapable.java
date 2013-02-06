package ca.hashbrown.snapable;

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
			// TODO set debug stuff
		}
	}

}
