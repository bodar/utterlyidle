package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.MatchFailure;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.Status;
import com.googlecode.yadic.Resolver;

import java.io.IOException;

public class MatchFailureHandler implements ResponseHandler<MatchFailure> {
    private final RendererHandler renderers;

    public MatchFailureHandler(RendererHandler renderers) {
        this.renderers = renderers;
    }

    public void handle(MatchFailure value, Resolver resolver, Response response) throws IOException {
        response.code(value.status());
        renderers.handle(value, resolver, response);
    }
}
