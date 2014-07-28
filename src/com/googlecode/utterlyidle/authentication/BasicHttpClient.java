package com.googlecode.utterlyidle.authentication;

import com.googlecode.utterlyidle.Base64;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.HttpClient;

import static com.googlecode.totallylazy.Strings.bytes;
import static com.googlecode.utterlyidle.HttpHeaders.AUTHORIZATION;
import static com.googlecode.utterlyidle.RequestBuilder.modify;
import static java.lang.String.format;

public class BasicHttpClient implements HttpClient {
    private final HttpClient client;
    private final Credential credential;

    public BasicHttpClient(final HttpClient client, final Credential credential) {
        this.credential = credential;
        this.client = client;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        return client.handle(modify(request).header(AUTHORIZATION, authorisation(credential)).build());
    }

    private String authorisation(final Credential credential) {
        return format("Basic %s", Base64.encode(bytes(credential.username + ":" + credential.password)));
    }
}
