package com.googlecode.utterlyidle.authentication;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.HttpClient;

import static com.googlecode.utterlyidle.HttpMessage.Builder.header;
import static java.lang.String.format;

public class BasicHttpClient implements HttpClient {
    private final HttpClient client;
    private final Credentials credentials;

    public BasicHttpClient(final HttpClient client, final Credentials credentials) {
        this.credentials = credentials;
        this.client = client;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        return client.handle(BasicAuthorisation.authorise(credentials, request));
    }
}
