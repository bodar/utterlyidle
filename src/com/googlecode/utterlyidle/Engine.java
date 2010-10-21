package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicate;
import com.googlecode.yadic.Resolver;

public interface Engine {
    void add(Class resource);
    void handle(Resolver resolver, Request request, Response response);

    <T> void addRenderer(Class<T> customClass, Renderer<T> renderer);

    void addResponseHandler(Predicate predicate, Class handler);
}
