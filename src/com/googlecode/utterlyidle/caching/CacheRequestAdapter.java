package com.googlecode.utterlyidle.caching;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.caching.HttpCache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.CacheRequest;

class CacheRequestAdapter extends CacheRequest {
    private final HttpCache cache;
    private final Request request;
    private final Response response;

    public CacheRequestAdapter(HttpCache cache, Request request, Response response) {
        this.cache = cache;
        this.request = request;
        this.response = response;
    }

    @Override
    public OutputStream getBody() throws IOException {
        return new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                super.close();
                cache.put(request, response.bytes(toByteArray()));
            }
        };
    }

    @Override
    public void abort() {

    }
}
