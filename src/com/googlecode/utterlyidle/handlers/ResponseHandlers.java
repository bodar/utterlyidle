package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;

public class ResponseHandlers extends HandlerRules<ResponseHandler> {
    @Override
    public void process(ResponseHandler handler, Response response) throws Exception {
        handler.handle(response);
    }
}
