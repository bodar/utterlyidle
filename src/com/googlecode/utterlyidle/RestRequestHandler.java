package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.yadic.Resolver;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;

public class RestRequestHandler implements RequestHandler {
    private final Engine restEngine;
    private final Resolver resolver;

    public RestRequestHandler(Engine restEngine, Resolver resolver) {
        this.restEngine = restEngine;
        this.resolver = resolver;
    }

    public void handle(Request request, Response response) throws Exception {
        final Either<MatchFailure, HttpMethodActivator> either = restEngine.findActivator(request);
        if (either.isLeft()) {
            handle(resolver, request, response.
                    status(either.left().status()).
                    header(CONTENT_TYPE, "text/html").
                    entity(either.left()));
        } else {
            handle(resolver, request, either.right().activate(resolver, request, response).
                    status(Status.OK));
        }
    }

    private void handle(Resolver resolver, Request request, Response response) {
        try {
            restEngine.responseHandlers().handle(response.entity(),resolver, response);
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }

    }


}
