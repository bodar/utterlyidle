package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;

import javax.ws.rs.core.HttpHeaders;

public class RestRequestHandler implements RequestHandler {
    private final Engine restEngine;
    private final Container container;

    public RestRequestHandler(Engine restEngine, Container container) {
        this.restEngine = restEngine;
        this.container = container;
    }

    public void handle(Request request, Response response) throws Exception {
        container.addInstance(Request.class, request).addInstance(Response.class, response);
        container.remove(Container.class);
        final Either<MatchFailure, HttpMethodActivator> either = restEngine.findActivator(request);
        if (either.isLeft()) {
            handle(ResponseBody.responseBody("text/html", either.left()), container, response);
        } else {
            final ResponseBody responseBody = either.right().activate(container, request);
            handle(responseBody, container, response);
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
