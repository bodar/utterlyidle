package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicate;
import com.googlecode.utterlyidle.handlers.RendererHandler;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.yadic.Resolver;

public interface Engine {
    void add(Class resource);
    void handle(Resolver resolver, Request request, Response response);

    RendererHandler renderers();

    ResponseHandlers responseHandlers();
}
