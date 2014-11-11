package com.snapable.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.client.UrlConnectionClient;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;

public abstract class BaseClient implements Client {

    private static HashMap<String, Object> resources;

    private Client wrapped;
    private String baseUrl;
    private boolean debug;

    public BaseClient(String baseUrl) {
        this(baseUrl, false);
    }

    public BaseClient(String baseUrl, boolean debug) {
        this.baseUrl = baseUrl;
        this.debug = debug;

        try {
            Class.forName("android.os.Build");
            if (hasOkHttpOnClasspath()) {
                wrapped = OkClientInstantiator.instantiate();
            } else {
                wrapped = new UrlConnectionClient();
            }
        } catch (ClassNotFoundException ignored) {
            if (hasOkHttpOnClasspath()) {
                wrapped = OkClientInstantiator.instantiate();
            } else {
                wrapped = new UrlConnectionClient();
            }
        }
    }

    protected RestAdapter.Builder createRestAdapterBuilder() {
        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setRequestInterceptor(getInterceptor());
        builder.setConverter(getConverter());
        builder.setEndpoint(baseUrl);
        if (debug)
            builder.setLogLevel(RestAdapter.LogLevel.FULL);
        builder.setClient(this);
        return builder;
    }

    public RestAdapter getRestAdapter() {
        return createRestAdapterBuilder().build();
    }

    /**
     * A helper method to get instance of resource.
     *
     * @param service the type of resource interface.
     * @return Instance of resource interface.
     */
    public synchronized <T> T create(Class<T> service){
        if (resources == null)
            resources = new HashMap<>(5);

        if (!resources.containsKey(service.getName()))
            resources.put(service.getName(), getRestAdapter().create(service));

        return (T) resources.get(service.getName());
    }

    @Override
    public Response execute(Request request) throws IOException {
        Request newRequest = sign(request);
        return wrapped.execute(newRequest);
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    // stuff to overwrite
    protected abstract Request sign(Request request);

    protected RequestInterceptor getInterceptor() {
        return new BaseInterceptor();
    }

    protected Converter getConverter() {
        // build the converter
        GsonBuilder builder = new GsonBuilder();
        //builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Gson gson = builder.create();
        return new GsonConverter(gson);
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
