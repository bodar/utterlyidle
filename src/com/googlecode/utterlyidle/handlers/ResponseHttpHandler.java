package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

public class ResponseHttpHandler implements HttpHandler {

    private final HttpHandler delegate;
    private final ResponseHandlersFinder responseHandlersFinder;

    public ResponseHttpHandler(final HttpHandler delegate, final ResponseHandlersFinder responseHandlersFinder) {
        this.delegate = delegate;
        this.responseHandlersFinder = responseHandlersFinder;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        return responseHandlersFinder.findAndHandle(request, delegate.handle(request));
    }
}
