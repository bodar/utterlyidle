package com.googlecode.utterlyidle;

import com.googlecode.yadic.Resolver;

public interface ResponseHandler<T> {
    void handle(Response response) throws Exception;
}

