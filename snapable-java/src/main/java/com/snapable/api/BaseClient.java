package com.snapable.api;

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

public abstract class BaseClient implements Client {

    private Client wrapped;
    private String mBaseUrl;
    private RestAdapter mRestAdapter;
    private HashMap<String, Object> mResources;

    private boolean mDebug = false;

    public BaseClient(String baseUrl) {
        this(baseUrl, false);
    }

    public BaseClient(String baseUrl, boolean debug) {
        mBaseUrl = baseUrl;
        mDebug = debug;
        mResources = new HashMap<>(5);

        if (hasOkHttpOnClasspath()) {
            wrapped = OkClientInstantiator.instantiate();
        } else {
            wrapped = new UrlConnectionClient();
        }
    }

    private RestAdapter.Builder createRestAdapterBuilder() {
        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setEndpoint(mBaseUrl);
        builder.setClient(this);

        // set the converter
        Converter converter = getConverter();
        if (converter != null)
            builder.setConverter(converter);

        // set the interceptor
        RequestInterceptor interceptor = getInterceptor();
        if (interceptor != null)
            builder.setRequestInterceptor(interceptor);

        // enable debug
        if (mDebug)
            builder.setLogLevel(RestAdapter.LogLevel.FULL);

        return builder;
    }

    public RestAdapter getRestAdapter() {
        if (mRestAdapter == null)
            mRestAdapter = createRestAdapterBuilder().build();
        return mRestAdapter;
    }

    @Override
    public Response execute(Request request) throws IOException {
        return wrapped.execute(sign(request));
    }

    /**
     * A helper method to get instance of resource.
     *
     * @param service the type of resource interface.
     * @return Instance of resource interface.
     */
    public synchronized <T> T create(Class<T> service) {
        @SuppressWarnings("unchecked")
        T serviceObject = (T) mResources.get(service.getName());
        if (serviceObject == null) {
            serviceObject = getRestAdapter().create(service);
            mResources.put(service.getName(), serviceObject);
        }
        return serviceObject;
    }

    protected Converter getConverter() {
        return null;
    }

    protected RequestInterceptor getInterceptor() {
        return null;
    }

    // stuff to overwrite
    protected Request sign(Request request) {
        return request;
    }

    /**
     * Determine whether or not OkHttp is present on the runtime classpath.
     */
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
