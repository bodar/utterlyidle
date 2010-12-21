package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.MatchFailure;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.yadic.Resolver;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;

public class MatchFailureHandler implements ResponseHandler<MatchFailure> {
    private final RendererHandler renderers;
    private final Resolver resolver;

    public MatchFailureHandler(RendererHandler renderers, Resolver resolver) {
        this.renderers = renderers;
        this.resolver = resolver;
    }

    public void handle(Response response) throws Exception {
        MatchFailure matchFailure = (MatchFailure) response.entity();
        response.status(matchFailure.status());
        response.header(CONTENT_TYPE, "text/html");
        renderers.with(resolver).handle(response);
    }
}
