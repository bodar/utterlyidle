package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.Status;
import com.googlecode.yadic.Resolver;

import java.io.IOException;

public class NullHandler implements ResponseHandler<Object> {
    public void handle(Object value, Resolver resolver, Response response) throws IOException {
        response.code(Status.NO_CONTENT);
    }
}
