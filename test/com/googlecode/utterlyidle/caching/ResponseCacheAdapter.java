package com.googlecode.utterlyidle.caching;

import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Requests;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Responses;
import com.googlecode.utterlyidle.Status;

import java.io.IOException;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.HttpURLConnection;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.Requests.request;
import static com.googlecode.utterlyidle.caching.CacheResponseAdapter.asCacheResponse;

public class ResponseCacheAdapter extends ResponseCache {
    private final HttpCache cache;

    public ResponseCacheAdapter(HttpCache cache) {
        this.cache = cache;
    }

    @Override
    public CacheResponse get(URI uri, String method, Map<String, List<String>> headers) throws IOException {
        return cache.get(request(method, uri(uri), headerParameters(headers), new byte[0])).
                map(asCacheResponse()).
                getOrNull();
    }


    @Override
    public CacheRequest put(final URI uri, final URLConnection conn) throws IOException {
        if(!(conn instanceof HttpURLConnection)){
            return null;
        }

        HttpURLConnection httpURLConnection = (HttpURLConnection) conn;
        final Request request = create(uri, httpURLConnection);
        Status status = Status.status(httpURLConnection);
        final Response response = Responses.response(status, headerParameters(httpURLConnection.getHeaderFields()));

        if(!cache.isCacheable(request, response)) {
            return null;
        }

        return new CacheRequestAdapter(cache, request, response);
    }

    private Request create(URI uri, HttpURLConnection httpURLConnection) {
        return Requests.request(httpURLConnection.getRequestMethod(), Uri.uri(uri), headerParameters(), new byte[0]);
    }

}
