package ca.hashbrown.snapable.api;

import com.snapable.api.private_v1.Client;

import ca.hashbrown.snapable.BuildConfig;

public class SnapClient extends Client {

    private static final String live_api_key = "***REMOVED***";
    private static final String live_api_secret = "***REMOVED***";

    /**
     * Get the client to talk to the API.
     *
     * @deprecated Use {@link SnapClient#getClient()}
     */
    public SnapClient() {
        super(Client.BASE_URL, live_api_key, live_api_secret);
    }

    public static Client getClient() {
        if (BuildConfig.DEBUG) {
            return new Client(Client.BASE_URL_DEV, "key123", "sec123");
        } else {
            return new Client(Client.BASE_URL, live_api_key, live_api_secret);
        }
    }
}
