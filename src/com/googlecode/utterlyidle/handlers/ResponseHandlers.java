package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.yadic.Resolver;

public class ResponseHandlers extends CompositeHandler<ResponseHandler> {
    @Override
    public void process(ResponseHandler handler, Object result, Resolver resolver, Response response) throws Exception {
        handler.handle(result, resolver, response);
    }
}
