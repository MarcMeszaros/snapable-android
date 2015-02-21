package com.snapable.api.private_v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.snapable.api.BaseClient;
import com.snapable.converters.SnapConverter;
import com.snapable.utils.SnapSigning;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.client.Header;
import retrofit.client.Request;
import retrofit.converter.Converter;
import retrofit.converter.JacksonConverter;

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
        // json object serializer
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // build the converter
        JacksonConverter jacksonConverter = new JacksonConverter(mapper);
        return new SnapConverter(jacksonConverter);
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
