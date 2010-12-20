package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.MatchFailure;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.yadic.Resolver;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;

public class MatchFailureHandler implements ResponseHandler<MatchFailure> {
    private final RendererHandler renderers;

    public MatchFailureHandler(RendererHandler renderers) {
        this.renderers = renderers;
    }

    public void handle(MatchFailure value, Resolver resolver, Response response) throws Exception {
        response.status(value.status());
        response.header(CONTENT_TYPE, "text/html");
        renderers.handle(value, resolver, response);
    }
}
