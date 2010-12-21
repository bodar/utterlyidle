package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Response;

public class CloseHandler implements HttpHandler {
    private final HttpHandler httpHandler;

    public CloseHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    public void handle(Request request, Response response) throws Exception {
        httpHandler.handle(request, response);
        response.close();
    }
}
