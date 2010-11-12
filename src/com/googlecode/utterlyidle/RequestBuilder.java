package com.googlecode.utterlyidle;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.googlecode.utterlyidle.FormParameters.formParameters;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.QueryParameters.queryParameters;
import static com.googlecode.utterlyidle.Request.request;
import static com.googlecode.utterlyidle.io.Url.url;

public class RequestBuilder {
    private final HeaderParameters headers = headerParameters();
    private final QueryParameters query = queryParameters();
    private final FormParameters form = formParameters();
    private InputStream input;
    private final String method;
    private final String path;

    public RequestBuilder(String method, String path) {
        this.method = method;
        this.path = path;
    }

    public RequestBuilder accepting(String value) {
        return withHeader(HttpHeaders.ACCEPT, value);
    }

    public RequestBuilder withHeader(String name, String value) {
        headers.add(name, value);
        return this;
    }

    public RequestBuilder withQuery(String name, String value) {
        query.add(name, value);
        return this;
    }

    public RequestBuilder withForm(String name, String value) {
        withHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        form.add(name, value);
        return this;
    }

    public RequestBuilder withInput(InputStream input) {
        this.input = input;
        return this;
    }

    public Request build() {
        return request(method, url(path + query.toString()), headers, input(form, input));
    }

    public static InputStream input(FormParameters form, InputStream input) {
        if(form.size() > 0){
            return new ByteArrayInputStream(form.toString().getBytes());
        }
        if(input == null){
            new ByteArrayInputStream(new byte[0]);
        }
        return input;
    }

    public static RequestBuilder get(String path) {
        return new RequestBuilder(HttpMethod.GET, path);
    }

    public static RequestBuilder post(String path) {
        return new RequestBuilder(HttpMethod.POST, path);
    }

    public static RequestBuilder put(String path) {
        return new RequestBuilder(HttpMethod.PUT, path);
    }

    public static RequestBuilder delete(String path) {
        return new RequestBuilder(HttpMethod.DELETE, path);
    }
}