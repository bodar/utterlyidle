package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.annotations.HttpMethod;

public class HeadRequestHandler implements HttpHandler {
    private final HttpHandler decorated;

    public HeadRequestHandler(HttpHandler decorated) {
        this.decorated = decorated;
    }

    @Override
    public Response handle(Request request) throws Exception {
        if (request.method().equals(HttpMethod.HEAD)) {
            Response response = decorated.handle(RequestBuilder.modify(request).method(HttpMethod.GET).build());
            return ResponseBuilder.modify(response).removeEntity().build();
        }

        return decorated.handle(request);
    }

}
