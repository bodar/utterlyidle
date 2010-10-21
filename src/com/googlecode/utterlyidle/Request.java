package com.googlecode.utterlyidle;

import java.io.InputStream;

import static com.googlecode.utterlyidle.BasePath.basePath;

public class Request {
    private final String method;
    private final BasePath base;
    private final String path;
    private final HeaderParameters headers;
    private final QueryParameters query;
    private final FormParameters form;
    private final InputStream input;

    public static Request request(String method, String path, HeaderParameters headers, QueryParameters query, FormParameters form, InputStream input) {
        return new Request(method, basePath(""), path, headers, query, form, input);
    }

    public Request(String method, BasePath base, String path, HeaderParameters headers, QueryParameters query, FormParameters form, InputStream input) {
        this.method = method;
        this.base = base;
        this.path = path;
        this.headers = headers;
        this.query = query;
        this.form = form;
        this.input = input;
    }

    public BasePath base() {
        return base;
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
}