package com.googlecode.utterlyidle;

import java.io.IOException;

public interface ResponseHandler<T> {
    void handle(T value, Response response) throws IOException;
}

