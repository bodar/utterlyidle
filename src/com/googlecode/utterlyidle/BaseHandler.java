package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.yadic.Resolver;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_HTML;

public class BaseHandler implements HttpHandler {
    private final ActivatorFinder activators;
    private final ResponseHandlers handlers;
    private final Resolver resolver;

    public BaseHandler(ActivatorFinder activators, ResponseHandlers handlers, Resolver resolver) {
        this.activators = activators;
        this.handlers = handlers;
        this.resolver = resolver;
    }

    public void handle(Request request, Response response) throws Exception {
        final Either<MatchFailure, HttpMethodActivator> either = activators.findActivator(request);
        if (either.isLeft()) {
            handlers.handle(request, response.
                        status(either.left().status()).
                        header(CONTENT_TYPE, TEXT_HTML).
                        entity(either.left()));
        } else {
            handlers.handle(request, either.right().activate(resolver, request, response).
                        status(Status.OK));
        }
    }
}
