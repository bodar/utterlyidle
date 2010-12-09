package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;

import javax.ws.rs.core.HttpHeaders;

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
            handle(ResponseBody.responseBody("text/html", either.left()), resolver, response);
        } else {
            final ResponseBody responseBody = either.right().activate(resolver, request);
            handle(responseBody, resolver, response);
        }
    }

    private void handle(ResponseBody responseBody, Resolver resolver, Response response) {
        try {
            response.header(HttpHeaders.CONTENT_TYPE, responseBody.mimeType());
            Object result = responseBody.value();
            restEngine.responseHandlers().handle(result, resolver, response);
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }

    }


}
