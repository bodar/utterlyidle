package com.googlecode.utterlyidle;

import com.googlecode.yadic.Resolver;

import java.io.IOException;

public interface ResponseHandler<T> {
    void handle(T value, Resolver resolver, Response response) throws IOException;
}

