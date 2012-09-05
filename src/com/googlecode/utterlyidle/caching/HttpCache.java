package com.googlecode.utterlyidle.caching;

import com.googlecode.totallylazy.Option;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

public interface HttpCache {
    Option<Response> get(Request request);

    boolean isCacheable(Request request, Response response);

    void put(Request request, Response response);
}
