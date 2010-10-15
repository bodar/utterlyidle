package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.servlet.BasePath;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

import static com.googlecode.utterlyidle.FormParameters.formParameters;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.QueryParameters.queryParameters;
import static com.googlecode.utterlyidle.servlet.BasePath.basePath;

public class Request {
    private final String method;
    private final BasePath base;
    private final String path;
    private final HeaderParameters headers;
    private final QueryParameters query;
    private final FormParameters form;
    private final InputStream input;

    public static Request request(HttpServletRequest request) {
        try {
            return new Request(request.getMethod(), basePath(request), request.getPathInfo(), headerParameters(request),
                    queryParameters(request), formParameters(request), request.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Request(String method, String path, HeaderParameters headers, QueryParameters query, FormParameters form, InputStream input) {
        this(method, basePath(""), path, headers, query, form, input);
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

    public BasePath getBase() {
        return base;
    }

    public FormParameters getForm() {
        return form;
    }

    public HeaderParameters getHeaders() {
        return headers;
    }

    public InputStream getInput() {
        return input;
    }

    public String getMethod() {
        return method;
    }

    public String path() {
        return path;
    }

    public QueryParameters getQuery() {
        return query;
    }
}