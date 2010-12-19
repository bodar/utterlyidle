package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.yadic.Resolver;

public class WriteMessageToResponseHandler  implements ResponseHandler<Object> {
    private final String message;

    public WriteMessageToResponseHandler(String message) {
        this.message = message;
    }

    public void handle(Object value, Resolver resolver, Response response) throws Exception {
        response.write(message);
    }
}