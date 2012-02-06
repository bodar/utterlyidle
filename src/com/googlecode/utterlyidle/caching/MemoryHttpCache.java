package com.googlecode.utterlyidle.caching;

import com.googlecode.totallylazy.Option;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.annotations.HttpMethod;

public class MemoryHttpCache implements HttpCache {
    private final CacheMap map;

    public MemoryHttpCache(int count) {
        map = new CacheMap(count);
    }

    public MemoryHttpCache() {
        this(100);
    }

    @Override
    public Option<Response> get(Request request) {
        return Option.option(map.get(request));
    }

    @Override
    public boolean isCacheable(Request request, Response response) {
        if (!request.method().equalsIgnoreCase(HttpMethod.GET)) {
            return false;
        }
        if (!response.status().equals(Status.OK)) {
            return false;
        }

        if (response.header(HttpHeaders.CACHE_CONTROL).toLowerCase().contains("public")) {
            return true;
        }

        return false;
    }

    @Override
    public void put(Request request, Response response) {
        if (isCacheable(request, response)) {
            map.put(request, response);
        }
    }

    public int size() {
        return map.size();
    }
}
