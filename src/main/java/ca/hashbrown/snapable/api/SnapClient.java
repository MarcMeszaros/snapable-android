package ca.hashbrown.snapable.api;

import com.snapable.api.private_v1.Client;

import ca.hashbrown.snapable.BuildConfig;

public class SnapClient extends Client {

    private static final String live_api_key = "***REMOVED***";
    private static final String live_api_secret = "***REMOVED***";

    private static SnapClient instance = null;
    private static Object instanceMutex = new Object();

    /**
     * Get the client to talk to the API.
     *
     * @deprecated Use {@link SnapClient#getClient()}
     */
    public SnapClient() {
        super(Client.BASE_URL, live_api_key, live_api_secret);
    }

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

    @Deprecated
    public static SnapClient getClient() {
        return getInstance();
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
                if (BuildConfig.DEBUG) {
                    instance = new SnapClient(Client.BASE_URL_DEV, "key123", "sec123");
                } else {
                    instance = new SnapClient(Client.BASE_URL, live_api_key, live_api_secret);
                }
            }
        }
        return instance;

    }
}
