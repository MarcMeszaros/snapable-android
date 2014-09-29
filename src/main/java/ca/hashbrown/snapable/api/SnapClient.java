package ca.hashbrown.snapable.api;


import android.os.Build;

import com.snapable.api.private_v1.Client;

import ca.hashbrown.snapable.BuildConfig;
import ca.hashbrown.snapable.Snapable;
import ca.hashbrown.snapable.utils.Network;

public class SnapClient extends Client {

    private static SnapClient instance = null;
    private static Object instanceMutex = new Object();

    private SnapClient(String key, String secret, boolean useDevApi) {
        super(key, secret, useDevApi);
    }

    /**
     * Get an instance of {@link ca.hashbrown.snapable.api.SnapClient}. It will also take care
     * of pointing to the dev API in debug builds and the production API for release builds.
     *
     * @return An instance of {@link ca.hashbrown.snapable.api.SnapClient}.
     */
    public static SnapClient getInstance() {
        if (instance == null) {
            synchronized (instanceMutex) {
                instance = new SnapClient(BuildConfig.API_KEY, BuildConfig.API_SECRET, BuildConfig.API_DEV);
            }
        }
        return instance;
    }

    /**
     * Determine if we can reach the API.
     *
     * @return a boolean representing if the Snapable API is reachable
     */
    public boolean isReachable() {
        Network netInfo = new Network(Snapable.getContext());
        return (netInfo.isConnected());
    }

}
