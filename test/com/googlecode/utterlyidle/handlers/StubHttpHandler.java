package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.utterlyidle.Responses.response;

public class StubHttpHandler implements HttpHandler {
    public Request request;
    public Response response = response();

    public Response handle(Request request) throws Exception {
        this.request = request;
        return response;
    }

    public void respondsWith(Response response) {
        this.response = response;
    }
}
