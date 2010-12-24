package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;

import java.io.IOException;
import java.io.OutputStream;

public abstract class DelegatingResponse implements Response {
    protected Response response;

    public DelegatingResponse(final Response response) {
        this.response = response;
    }

    public Status status() {
        return response.status();
    }

    public String header(String name) {
        return response.header(name);
    }

    public Iterable<String> headers(String name) {
        return response.headers(name);
    }

    public OutputStream output() {
        return response.output();
    }

    public Object entity() {
        return response.entity();
    }

    public void close() throws IOException {
        response.close();
    }
}
