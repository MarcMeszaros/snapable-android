package com.snapable.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.RestAdapter;
import retrofit.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SnapClient implements Client {
    Client wrapped = null;
    SnapInterceptor interceptor = null;
    SnapConverter converter = null;

    private static final String live_api_key = "***REMOVED***";
    private static final String live_api_secret = "***REMOVED***";

    public SnapClient() {
        try {
            Class.forName("android.os.Build");
            if (hasOkHttpOnClasspath()) {
                wrapped = OkClientInstantiator.instantiate();
            } else {
                wrapped = new UrlConnectionClient();
            }
            interceptor = new SnapInterceptor();
            // build the converter
            GsonBuilder builder = new GsonBuilder();
            builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Gson gson = builder.create();
            converter = new SnapConverter(gson);
        } catch (ClassNotFoundException ignored) {
            if (hasOkHttpOnClasspath()) {
                wrapped = OkClientInstantiator.instantiate();
            } else {
                wrapped = new UrlConnectionClient();
            }
            interceptor = new SnapInterceptor();
            // build the converter
            GsonBuilder builder = new GsonBuilder();
            builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Gson gson = builder.create();
            converter = new SnapConverter(gson);
        }
    }

    public SnapClient(Client client) {
        wrapped = client;
        interceptor = new SnapInterceptor();
        // build the converter
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Gson gson = builder.create();
        converter = new SnapConverter(gson);
    }

    public RestAdapter getRestAdapter() {
        // build the RestAdapter
        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setRequestInterceptor(interceptor);
        builder.setConverter(converter);
        builder.setClient(this);

        // set client values based on build mode
        builder.setEndpoint("http://devapi.snapable.com/" + SnapApi.api_version + "/");

        return builder.build();
    }

    @Override public Response execute(Request request) throws IOException {
        Request newRequest = sign(request);
        return wrapped.execute(newRequest);
    }

    private Request sign(Request request) {

        // get the path
        String pattern = "https?:\\/\\/(\\w+\\.?)\\w+\\.\\w+([\\w\\/]+).*";
        String path = request.getUrl().replaceAll(pattern, "$2");

        HashMap<String, String> vals = SnapApi.sign(request.getMethod(), path);
        StringBuilder sb = new StringBuilder();
        sb.append("key=\"");
        sb.append(vals.get("api_key"));
        sb.append("\",signature=\"");
        sb.append(vals.get("signature"));
        sb.append("\",nonce=\"");
        sb.append(vals.get("nonce"));
        sb.append("\",timestamp=\"");
        sb.append(vals.get("timestamp"));
        sb.append("\"");
        String authString = sb.toString();

        // magic
        List<Header> headers = request.getHeaders();
        Header h = new Header("Authorization", "SNAP "+authString);

        // add the signature to the list of headers
        List<Header> headerList = new ArrayList<Header>();
        headerList.addAll(headers);
        headerList.add(h);

        // return the signed request
        Request signedRequest = new Request(request.getMethod(), request.getUrl(), headerList, request.getBody());
        return signedRequest;
    }

    /** Determine whether or not OkHttp is present on the runtime classpath. */
    private static boolean hasOkHttpOnClasspath() {
        try {
            Class.forName("com.squareup.okhttp.OkHttpClient");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Indirection for OkHttp class to prevent VerifyErrors on Android 2.0 and earlier when the
     * dependency is not present.
     */
    private static class OkClientInstantiator {
        static Client instantiate() {
            return new OkClient();
        }
    }
}
