package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.io.Url;

public class Requests {
    public static MemoryRequest request(String method, Url requestUri, HeaderParameters headers, byte[] input) {
        return new MemoryRequest(method, requestUri, headers, input);
    }

    public static Request request(String method, String path, QueryParameters query, HeaderParameters headers, byte[] input) {
        return request(method, Url.url(path + query.toString()), headers, input);
    }
}
