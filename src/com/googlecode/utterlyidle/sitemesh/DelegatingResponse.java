package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;

import java.io.IOException;
import java.io.OutputStream;

public class DelegatingResponse implements Response {
    protected final Response response;
    private Object entity;

    public DelegatingResponse(final Response response) {
        this.response = response;
    }

    public Status status() {
        return response.status();
    }

    public Response status(Status value) {
        return response.status(value);
    }

    public String header(String name) {
        return response.header(name);
    }

    public Iterable<String> headers(String name) {
        return response.headers(name);
    }

    public Response header(String name, String value) {
        return response.header(name, value);
    }

    public OutputStream output() {
        return response.output();
    }

    public Object entity() {
        return entity;
    }

    public Response entity(Object value) {
        entity = value;
        return this;
    }

    public void close() throws IOException {
        response.close();
    }
}
