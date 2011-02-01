package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.utterlyidle.handlers.ResponseHandlersFinder;
import com.googlecode.yadic.Resolver;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.Responses.response;
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

    public Response handle(Request request) throws Exception {
        final Either<MatchFailure, HttpMethodActivator> either = activators.findActivator(request);
        if (either.isLeft()) {
            return findAndHandle(request, response(
                    either.left().status(),
                    headerParameters(pair(CONTENT_TYPE, TEXT_HTML)),
                    either.left()));
        } else {
            return findAndHandle(request, either.right().activate(resolver, request, response()));
        }
    }

    private Response findAndHandle(Request request, Response response) throws Exception {
        return handlers.findHandler(request, response).handle(response);
    }
}
