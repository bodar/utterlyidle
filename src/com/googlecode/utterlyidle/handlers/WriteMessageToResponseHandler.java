package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;


public class WriteMessageToResponseHandler implements ResponseHandler {
    private final String message;

    public WriteMessageToResponseHandler(String message) {
        this.message = message;
    }

    public Response handle(final Response response) throws Exception {
        return response.bytes(message.getBytes("UTF-8"));
    }

}