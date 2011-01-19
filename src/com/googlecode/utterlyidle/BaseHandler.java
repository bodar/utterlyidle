package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.utterlyidle.handlers.ResponseHandlersFinder;
import com.googlecode.yadic.Resolver;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_HTML;

public class BaseHandler implements HttpHandler {
    private final ActivatorFinder activators;
    private final ResponseHandlersFinder handlers;
    private final Resolver resolver;

    public BaseHandler(ActivatorFinder activators, ResponseHandlersFinder handlers, Resolver resolver) {
        this.activators = activators;
        this.handlers = handlers;
        this.resolver = resolver;
    }

    public void handle(Request request, Response response) throws Exception {
        final Either<MatchFailure, HttpMethodActivator> either = activators.findActivator(request);
        if (either.isLeft()) {
            findAndHandle(request, response.
                    status(either.left().status()).
                    header(CONTENT_TYPE, TEXT_HTML).
                    entity(either.left()));
        } else {
            findAndHandle(request, either.right().activate(resolver, request, response).
                    status(Status.OK));
        }
    }

    private void findAndHandle(Request request, Response response) throws Exception {
        handlers.findHandler(request, response).handle(response);
    }
}