package com.googlecode.utterlyidle;

import com.googlecode.yadic.Resolver;

public interface ResponseHandler<T> {
    void handle(T value, Resolver resolver, Response response) throws Exception;
}

