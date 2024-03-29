package ca.hashbrown.snapable.api;

import android.content.Context;

import com.snapable.api.private_v1.Client;

import java.util.HashMap;

import ca.hashbrown.snapable.BuildConfig;
import ca.hashbrown.snapable.Snapable;
import ca.hashbrown.snapable.utils.Network;

public class SnapClient extends Client {

    private static SnapClient instance;

    private SnapClient(String key, String secret, String baseUrl, boolean debug) {
        super(key, secret, baseUrl, debug);
    }

    /**
     * Get an instance of {@link ca.hashbrown.snapable.api.SnapClient}. It will also take care
     * of pointing to the dev API in debug builds and the production API for release builds.
     *
     * @return An instance of {@link ca.hashbrown.snapable.api.SnapClient}.
     */
    public static synchronized SnapClient getInstance() {
        if (instance == null)
            instance = new SnapClient(BuildConfig.API_KEY, BuildConfig.API_SECRET, BuildConfig.API_BASE_URL, BuildConfig.DEBUG);

        return instance;
    }

    /**
     * A helper method to get instance of resource.
     *
     * @param service the type of resource interface.
     * @return Instance of resource interface.
     */
    public static synchronized <T> T getResource(Class<T> service){
        return getInstance().create(service);
    }

    /**
     * Determine if we can reach the API.
     *
     * @return a boolean representing if the Snapable API is reachable
     */
    public static boolean isReachable(Context context) {
        Network netInfo = new Network(context);
        return (netInfo.isConnected());
    }

}
