package com.googlecode.utterlyidle.rendering.exceptions;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

public class LastExceptionsHandler implements HttpHandler {
    private final HttpHandler handler;
    private final LastExceptions lastExceptions;

    public LastExceptionsHandler(final HttpHandler handler, final LastExceptions lastExceptions) {
        this.handler = handler;
        this.lastExceptions = lastExceptions;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        try {
            return handler.handle(request);
        } catch (Exception e) {
            lastExceptions.put(request, e);
            throw e;
        }
    }
}
