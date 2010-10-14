package com.googlecode.utterlyidle;

import com.googlecode.yadic.Resolver;

public interface Engine {
    void add(Class resource);
    void handle(Resolver resolver, Request request, Response response);
}
