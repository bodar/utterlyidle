package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;

public class ReturnResponseHandler implements HttpHandler {
    private final Response response;

    public ReturnResponseHandler(Response response) {
        this.response = response;
    }

    public Response handle(Request request) throws Exception {
        return response;
    }

    public static HttpHandler returnsResponse(final Response response) {
        return new ReturnResponseHandler(response);
    }

    public static HttpHandler returnsResponse(final ResponseBuilder builder) {
        return new ReturnResponseHandler(builder.build());
    }
}
