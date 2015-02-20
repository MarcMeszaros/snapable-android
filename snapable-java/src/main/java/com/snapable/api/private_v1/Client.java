package com.snapable.api.private_v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.snapable.api.BaseClient;
import com.snapable.utils.SnapSigning;
import com.snapable.converters.SnapConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.client.Header;
import retrofit.client.Request;
import retrofit.converter.Converter;

public class Client extends BaseClient {

    public static final String VERSION = "private_v1";
    private static final String BASE_URL = "https://api.snapable.com/" + VERSION + "/";
    private static final String BASE_URL_DEV = "http://devapi.snapable.com/" + VERSION + "/";

    private final SnapSigning snapSigning;

    public Client(String key, String secret) {
        this(key, secret, false, false);
    }

    public Client(String key, String secret, boolean useDevApi, boolean debug) {
        super(!useDevApi ? BASE_URL : BASE_URL_DEV, debug);
        snapSigning = new SnapSigning(VERSION, key, secret);
    }

    @Override
    protected Converter getConverter() {
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

        HashMap<String, String> vals = snapSigning.sign(request.getMethod(), path);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("key=\"%s\"", vals.get("api_key")));
        sb.append(String.format(",signature=\"%s\"", vals.get("signature")));
        sb.append(String.format(",nonce=\"%s\"", vals.get("nonce")));
        sb.append(String.format(",timestamp=\"%s\"", vals.get("timestamp")));

        // magic
        Header auth = new Header("Authorization", "SNAP " + sb.toString());

        // add the signature to the list of headers
        List<Header> headers = new ArrayList<>(request.getHeaders());
        headers.add(auth);

        // return the signed request
        return new Request(request.getMethod(), request.getUrl(), headers, request.getBody());
    }

}
