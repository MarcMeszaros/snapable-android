package ca.hashbrown.snapable.api;

import com.snapable.api.SnapApi;
import com.snapable.api.private_v1.Client;

import ca.hashbrown.snapable.BuildConfig;

import retrofit.RestAdapter;

public class SnapClient extends Client {

    private static final String live_api_key = "***REMOVED***";
    private static final String live_api_secret = "***REMOVED***";

    public RestAdapter getRestAdapter() {
        // build the RestAdapter
        RestAdapter.Builder builder = createRestAdapterBuilder();

        // set client values based on build mode
        if (BuildConfig.DEBUG) {
            builder.setEndpoint(Client.BASE_URL_DEV);
        } else {
            builder.setEndpoint(Client.BASE_URL);
            SnapApi.setApiKeySecret(live_api_key, live_api_secret);
        }

        return builder.build();
    }
}
