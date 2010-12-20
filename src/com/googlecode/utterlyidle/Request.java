package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.io.HierarchicalPath;
import com.googlecode.utterlyidle.io.Url;

import javax.ws.rs.core.HttpHeaders;
import java.io.InputStream;

import static com.googlecode.utterlyidle.io.Converter.asString;

public class Request {
    private final String method;
    private Url url;
    private final InputStream input;
    private final HeaderParameters headers;
    private BasePath basePath;
    private QueryParameters query;
    private FormParameters form;

    public static Request request(String method, Url requestUri, HeaderParameters headers, InputStream input) {
        return new Request(method, requestUri, headers, input, BasePath.basePath("/"));
    }

    public static Request request(String method, String path, QueryParameters query, HeaderParameters headers, InputStream input) {
        return request(method, Url.url(path + query.toString()), headers, input);
    }

    protected Request(String method, Url url, HeaderParameters headers, InputStream input, BasePath basePath) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.input = input;
        this.basePath = basePath;
    }

    public String method() {
        return method;
    }

    public Url url() {
        return url;
    }

    public Request url(Url url) {
        this.url= url;
        this.query = null;
        return this;
    }

    public InputStream input() {
        return input;
    }

    public HeaderParameters headers() {
        return headers;
    }

    public QueryParameters query() {
        if (query == null) {
            query = QueryParameters.parse(url().getQuery());
        }
        return query;
    }

    public FormParameters form() {
        if (form == null) {
            if ("application/x-www-form-urlencoded".equals(headers().getValue(HttpHeaders.CONTENT_TYPE))) {
                form = FormParameters.parse(asString(input()));
            } else {
                form = FormParameters.formParameters();
            }
        }
        return form;
    }

    @Override
    public String toString() {
        return String.format("%s %s HTTP/1.1\n%s\n%s", method, url, headers(), inputAsRequestString());
    }

    private String inputAsRequestString() {
        String input = asString(input());
        return input.length() == 0 ? "" : String.format("Content-length: %s\n\n%s", input.length(), input);
    }

    public ResourcePath resourcePath() {
        return ResourcePath.resourcePath(removeBase().toString());
    }

    private HierarchicalPath removeBase() {
        final HierarchicalPath path = url().path();
        final BasePath base = basePath();
        if(path.containedBy(base)) {
            return path.remove(base);
        }
        return path;
    }

    public BasePath basePath() {
        return basePath;
    }
}