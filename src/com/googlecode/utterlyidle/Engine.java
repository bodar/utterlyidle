package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.utterlyidle.handlers.RendererHandler;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.yadic.Resolver;

public interface Engine {
    void add(Class resource);

    RendererHandler renderers();

    ResponseHandlers responseHandlers();


    Either<MatchFailure, HttpMethodActivator> findActivator(Request request);
}
