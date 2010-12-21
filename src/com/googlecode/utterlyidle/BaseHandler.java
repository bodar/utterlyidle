package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.yadic.Resolver;

public class BaseHandler implements HttpHandler {
    private final ActivatorFinder activatorFinder;
    private final ResponseHandlers responseHandlers;
    private final Resolver resolver;

    public BaseHandler(ActivatorFinder activatorFinder, ResponseHandlers responseHandlers, Resolver resolver) {
        this.activatorFinder = activatorFinder;
        this.responseHandlers = responseHandlers;
        this.resolver = resolver;
    }

    public void handle(Request request, Response response) throws Exception {
        final Either<MatchFailure, HttpMethodActivator> either = activatorFinder.findActivator(request);
        if (either.isLeft()) {
            handle(resolver, request, response.
                    entity(either.left()));
        } else {
            handle(resolver, request, either.right().activate(resolver, request, response).
                    status(Status.OK));
        }
    }

    private void handle(Resolver resolver, Request request, Response response) throws Exception {
        responseHandlers.handle(response.entity(), resolver, response);
    }
}
