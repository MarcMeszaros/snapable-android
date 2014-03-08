package com.snapable.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

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
    private Client wrapped;
    private String baseUrl;

    public BaseClient() {
    }

    public BaseClient(String baseUrl) {
        this.baseUrl = baseUrl;

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

    public RestAdapter.Builder createRestAdapterBuilder() {
        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setRequestInterceptor(this.getInterceptor());
        builder.setConverter(this.getConverter());
        builder.setEndpoint(this.baseUrl);
        builder.setClient(this);
        return builder;
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
