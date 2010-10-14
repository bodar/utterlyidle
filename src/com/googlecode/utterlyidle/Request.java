package org.webfabric.rest;

import com.googlecode.utterlyidle.servlet.BasePath;
import com.googlecode.utterlyidle.HeaderParameters;

import javax.servlet.http.HttpServletRequest;

public class Request{
  private final String method;
  private final BasePath base;
    private final String path;
    private final HeaderParameters headers;
    private final QueryParameters query;
    private final FormParameters form;
    private final InputStream input;

    public Request(HttpServletRequest request){
    this(
            request.getMethod(),
            basePath(request),
            request.getPathInfo(),
            headerParameters(request),
            queryParameters(request),
            formParameters(request),
            request.getInputStream());
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
  }
}