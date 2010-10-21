package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.yadic.Resolver;

import java.io.IOException;

public class ResponseHandlers extends CompositeHandler<ResponseHandler> {
    @Override
    public void process(ResponseHandler handler, Object result, Resolver resolver, Response response) throws IOException {
        handler.handle(result, resolver, response);
    }
}
