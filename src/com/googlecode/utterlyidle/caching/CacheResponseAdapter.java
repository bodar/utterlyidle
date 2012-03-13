package com.googlecode.utterlyidle.caching;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.utterlyidle.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CacheResponse;
import java.util.List;
import java.util.Map;

import static com.googlecode.totallylazy.Arrays.list;
import static java.lang.String.format;

public class CacheResponseAdapter extends CacheResponse {
    private final Response response;

    public CacheResponseAdapter(Response response) {
        this.response = response;
    }

    @Override
    public Map<String, List<String>> getHeaders() throws IOException {
        Map<String, List<String>> map = response.headers().toMap();
        map.put(null, list(format("HTTP/1.1 %s", response.status())));
        return map;
    }

    @Override
    public InputStream getBody() throws IOException {
        return new ByteArrayInputStream((byte[])response.entity());
    }

    public static Callable1<Response, CacheResponse> asCacheResponse() {
        return new Callable1<Response, CacheResponse>() {
            @Override
            public CacheResponse call(Response response) throws Exception {
                return new CacheResponseAdapter(response);
            }
        };
    }

}
