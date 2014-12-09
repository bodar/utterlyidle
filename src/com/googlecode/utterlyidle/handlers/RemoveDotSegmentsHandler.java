package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.utterlyidle.RequestBuilder.modify;

public class RemoveDotSegmentsHandler implements HttpHandler {
    private final HttpHandler httpHandler;

    public RemoveDotSegmentsHandler(final HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        return httpHandler.handle(modify(request).uri(request.uri().removeDotSegments()).build());
    }
}
