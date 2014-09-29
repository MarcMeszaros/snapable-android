package com.snapable.api.private_v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.snapable.api.BaseClient;
import com.snapable.api.BaseInterceptor;
import com.snapable.api.SnapApi;
import com.snapable.api.SnapConverter;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.*;
import retrofit.converter.Converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Client extends BaseClient {

    public static final String VERSION = "private_v1";
    private static final String BASE_URL = "https://api.snapable.com/" + VERSION + "/";
    private static final String BASE_URL_DEV = "http://devapi.snapable.com/" + VERSION + "/";

    private final SnapApi snapApi;

    public Client(String key, String secret) {
        this(key, secret, false);
    }

    public Client(String key, String secret, boolean useDevApi) {
        super(!useDevApi ? BASE_URL : BASE_URL_DEV);
        snapApi = new SnapApi(VERSION, key, secret);
    }

    public RestAdapter getRestAdapter() {
        return createRestAdapterBuilder().build();
    }

    @Override
    public Converter getConverter() {
        // build the converter
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Gson gson = builder.create();
        return new SnapConverter(gson);
    }

    @Override
    protected Request sign(Request request) {
        // get the path
        String pattern = "https?:\\/\\/(\\w+\\.?)\\w+\\.\\w+([\\w\\/]+).*";
        String path = request.getUrl().replaceAll(pattern, "$2");

        HashMap<String, String> vals = snapApi.sign(request.getMethod(), path);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("key=\"%s\"", vals.get("api_key")));
        sb.append(String.format(",signature=\"%s\"", vals.get("signature")));
        sb.append(String.format(",nonce=\"%s\"", vals.get("nonce")));
        sb.append(String.format(",timestamp=\"%s\"", vals.get("timestamp")));
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

}
