package com.googlecode.utterlyidle.authentication;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.HttpClient;

public class BasicHttpClient implements HttpClient {
    private final HttpClient client;
    private final ClientCredentials credentials;

    public BasicHttpClient(final HttpClient client, final ClientCredentials credentials) {
        this.credentials = credentials;
        this.client = client;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        return client.handle(request);
    }
}
