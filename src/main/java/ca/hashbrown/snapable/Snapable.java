package ca.hashbrown.snapable;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;
import com.google.analytics.tracking.android.GoogleAnalytics;

import ca.hashbrown.snapable.utils.CrashlyticsTree;
import timber.log.Timber;

/**
 * Set some defaults for the Android application based on the build type.
 *
 * @author Marc Meszaros <marc@snapable.com>
 */
public class Snapable extends Application {

	private static Snapable instance;

	@Override
	public void onCreate() {
		super.onCreate();
        instance = this;
        Timber.v("+++ BUILD VERSION: %s(%d) +++", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);

        if(BuildConfig.DEBUG) {
		    // set google analytics to be in debug mode
			GoogleAnalytics.getInstance(this).setAppOptOut(true);
		}

        // Initialize the logging library
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashlyticsTree(this));
        }
	}

    /**
     * Get the application context.
     *
     * @return the application {@link android.content.Context}
     */
    @Deprecated
    public static Context getContext() {
        return instance;
    }

    /**
     * Return the application's version code defined in the manifest file.
     *
     * @return int of the android application version code
     */
    @Deprecated
    public static int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    /**
     * Return the applications version name defined in the manifest file.
     *
     * @return a string representing the version name
     */
    @Deprecated
    public static String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }

}
