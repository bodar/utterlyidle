package com.googlecode.utterlyidle.caching;

import com.googlecode.totallylazy.Option;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.annotations.HttpMethod;

import static com.googlecode.utterlyidle.HttpHeaders.CACHE_CONTROL;
import static com.googlecode.utterlyidle.Response.methods.header;

public class MemoryHttpCache implements HttpCache {
    private final CacheMap map;

    public MemoryHttpCache(int count) {
        map = new CacheMap(count);
    }

    public MemoryHttpCache() {
        this(100);
    }

    @Override

    public synchronized Option<Response> get(Request request) {
        return Option.option(map.get(request));
    }

    @Override
    public synchronized boolean isCacheable(Request request, Response response) {
        if (!request.method().equalsIgnoreCase(HttpMethod.GET)) {
            return false;
        }
        if (!response.status().equals(Status.OK)) {
            return false;
        }

        if (response.headers().contains(CACHE_CONTROL) && header(response, CACHE_CONTROL).toLowerCase().contains("public")) {
            return true;
        }

        return false;
    }

    @Override
    public synchronized void put(Request request, Response response) {
        if (isCacheable(request, response)) {
            map.put(request, response);
        }
    }

    public int size() {
        return map.size();
    }
}
