package com.googlecode.utterlyidle.authentication;

import com.googlecode.utterlyidle.BaseUri;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.Set;

import static com.googlecode.totallylazy.predicates.Predicates.in;
import static com.googlecode.utterlyidle.HttpHeaders.AUTHORIZATION;
import static com.googlecode.utterlyidle.HttpHeaders.WWW_AUTHENTICATE;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.Status.UNAUTHORIZED;
import static com.googlecode.utterlyidle.authentication.BasicGrammar.parseCredential;
import static java.lang.String.format;

public class BasicHttpHandler implements HttpHandler {
    private final HttpHandler handler;
    private final BaseUri baseUri;
    private final Set<? extends Credentials> credentials;

    public BasicHttpHandler(final HttpHandler handler, final BaseUri baseUri, final Set<? extends Credentials> credentials) {
        this.handler = handler;
        this.baseUri = baseUri;
        this.credentials = credentials;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        if (notAuthenticated(request)) {
            return response(UNAUTHORIZED).
                    header(WWW_AUTHENTICATE, format("Basic realm=\"%s\"", baseUri.value().host())).
                    build();
        }
        return handler.handle(request);
    }

    private boolean notAuthenticated(final Request request) {
        return !request.headers().valueOption(AUTHORIZATION).
                flatMap(parseCredential).exists(in(credentials));
    }

}
