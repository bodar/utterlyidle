package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.io.Url;

import java.io.InputStream;

public class Requests {
    public static DefaultRequest request(String method, Url requestUri, HeaderParameters headers, InputStream input) {
        return new DefaultRequest(method, requestUri, headers, input, BasePath.basePath("/"));
    }

    public static Request request(String method, String path, QueryParameters query, HeaderParameters headers, InputStream input) {
        return request(method, Url.url(path + query.toString()), headers, input);
    }
}
