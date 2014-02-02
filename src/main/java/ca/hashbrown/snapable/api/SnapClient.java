package ca.hashbrown.snapable.api;

import com.snapable.api.SnapApi;

import ca.hashbrown.snapable.BuildConfig;

import retrofit.RestAdapter;

public class SnapClient extends com.snapable.api.SnapClient {

    private static final String live_api_key = "***REMOVED***";
    private static final String live_api_secret = "***REMOVED***";

    @Override
    public RestAdapter getRestAdapter() {
        // build the RestAdapter
        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setRequestInterceptor(interceptor);
        builder.setConverter(converter);
        builder.setClient(this);

        // set client values based on build mode
        if (BuildConfig.DEBUG) {
            builder.setEndpoint("http://devapi.snapable.com/" + SnapApi.api_version + "/");
        } else {
            builder.setEndpoint("https://api.snapable.com/" + SnapApi.api_version + "/");
            SnapApi.setApiKeySecret(live_api_key, live_api_secret);
        }

        return builder.build();
    }
}
