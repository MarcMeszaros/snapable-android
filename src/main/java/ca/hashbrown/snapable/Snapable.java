package ca.hashbrown.snapable;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;
import com.google.analytics.tracking.android.GoogleAnalytics;

/**
 * Set some defaults for the Android application based on the build type.
 *
 * @author Marc Meszaros <marc@snapable.com>
 */
public class Snapable extends Application {

	private static final String TAG = "Snapable";
    private static Snapable instance;

	@Override
	public void onCreate() {
		super.onCreate();
        instance = this;

		// if we are in release mode
		if(!BuildConfig.DEBUG) {
			Log.i(TAG, "Starting in release mode.");

			// set google analytics to be in release mode
			// TODO implement release mode
		} else {
			Log.i(TAG, "Starting in debug mode.");

			// set google analytics to be in debug mode
			GoogleAnalytics.getInstance(this).setAppOptOut(true);
		}
	}

    /**
     * Get the application context.
     *
     * @return the application context
     */
    public static Context getContext() {
        return instance;
    }

    /**
     * Return the application's version code defined in the manifest file.
     *
     * @return int of the android application version code
     */
    public static int getVersionCode() {
        try {
            PackageInfo packageInfo = instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    /**
     * Return the applications version name defined in the manifest file.
     *
     * @return a string representing the version name
     */
    public static String getVersionName() {
        try {
            PackageInfo packageInfo = instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            return null;
        }
    }

}
