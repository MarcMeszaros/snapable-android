package ca.hashbrown.snapable;

import android.app.Application;
import android.content.Context;

import com.google.analytics.tracking.android.GoogleAnalytics;

import java.io.File;

import ca.hashbrown.snapable.utils.CrashlyticsTree;
import timber.log.Timber;

/**
 * Set some defaults for the Android application based on the build type.
 *
 * @author Marc Meszaros <marc@snapable.com>
 */
public class Snapable extends Application {

	private static Snapable instance;

    public static File FILE_CACHE_DIR;

	@Override
	public void onCreate() {
		super.onCreate();
        instance = this;
        Timber.v("+++ BUILD VERSION: %s(%d) +++", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);

        // Some context sensitive variables.
        FILE_CACHE_DIR = getExternalCacheDir();

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

}
