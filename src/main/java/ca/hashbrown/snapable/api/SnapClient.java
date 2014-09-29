package ca.hashbrown.snapable.api;


import com.snapable.api.private_v1.Client;

import ca.hashbrown.snapable.BuildConfig;
import ca.hashbrown.snapable.Snapable;
import ca.hashbrown.snapable.utils.Network;

public class SnapClient extends Client {

    private static SnapClient instance = null;
    private static Object instanceMutex = new Object();

    /**
     * Build a new instance of the SnapClient
     *
     * @param baseUrl the base API url
     * @param key the API key
     * @param secret the API secret
     */
    public SnapClient(String baseUrl, String key, String secret) {
        super(baseUrl, key, secret);
    }

    /**
     * Get an instance of {@link ca.hashbrown.snapable.api.SnapClient}. It will also take care
     * of point to the dev API in debug builds and the production API for release builds.
     *
     * @return An instance of {@link ca.hashbrown.snapable.api.SnapClient}.
     */
    public static SnapClient getInstance() {
        if (instance == null) {
            synchronized (instanceMutex) {
                String baseUrl = (!BuildConfig.DEBUG) ? Client.BASE_URL : Client.BASE_URL_DEV;
                instance = new SnapClient(baseUrl, BuildConfig.API_KEY, BuildConfig.API_SECRET);
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
