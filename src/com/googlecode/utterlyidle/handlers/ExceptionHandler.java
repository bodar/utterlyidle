package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.Status;
import com.googlecode.yadic.Resolver;

public class ExceptionHandler implements ResponseHandler<Exception> {
    private final Status status;
    private final RendererHandler renderers;

    public ExceptionHandler(Status status, RendererHandler renderers) {
        this.status = status;
        this.renderers = renderers;
    }

    public void handle(Exception value, Resolver resolver, Response response) throws Exception {
        response.code(status);
        renderers.handle(value, resolver, response);
    }
}
