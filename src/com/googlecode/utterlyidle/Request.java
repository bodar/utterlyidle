package com.googlecode.utterlyidle;

import java.io.InputStream;

public class Request {
    private final String method;
    private final String path;
    private final HeaderParameters headers;
    private final QueryParameters query;
    private final FormParameters form;
    private final InputStream input;

    public static Request request(String method, String path, HeaderParameters headers, QueryParameters query, FormParameters form, InputStream input) {
        return new Request(method, path, headers, query, form, input);
    }

    public Request(String method, String path, HeaderParameters headers, QueryParameters query, FormParameters form, InputStream input) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.query = query;
        this.form = form;
        this.input = input;
    }

    public FormParameters form() {
        return form;
    }

    public HeaderParameters headers() {
        return headers;
    }

    public InputStream input() {
        return input;
    }

    public String method() {
        return method;
    }

    public String path() {
        return path;
    }

    public QueryParameters query() {
        return query;
    }

    @Override
    public String toString() {
        return String.format("%s %s%s HTTP/1.1", method, path, query);
    }
}