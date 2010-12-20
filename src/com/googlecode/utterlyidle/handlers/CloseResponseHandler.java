package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestHandler;
import com.googlecode.utterlyidle.Response;

public class CloseResponseHandler implements RequestHandler{
    private final RequestHandler requestHandler;

    public CloseResponseHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public void handle(Request request, Response response) throws Exception {
        requestHandler.handle(request, response);
        response.close();
    }
}
