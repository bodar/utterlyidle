package com.googlecode.utterlyidle.authentication;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.HttpClient;

public class DigestHttpClient implements HttpClient {
    private final HttpClient client;

    public DigestHttpClient(final HttpClient client) {
        this.client = client;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        return client.handle(request);
    }
}
