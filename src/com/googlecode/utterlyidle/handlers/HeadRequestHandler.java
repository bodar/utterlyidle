package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.annotations.HttpMethod;
import com.googlecode.utterlyidle.bindings.BindingMatcher;

public class HeadRequestHandler implements HttpHandler {
    private final HttpHandler decorated;
    private final BindingMatcher matcher;

    public HeadRequestHandler(HttpHandler decorated, BindingMatcher matcher) {
        this.decorated = decorated;
        this.matcher = matcher;
    }

    @Override
    public Response handle(Request request) throws Exception {
        if (request.method().equals(HttpMethod.HEAD) && matcher.match(request).isLeft()) {
            Response response = decorated.handle(Request.Builder.modify(request, Request.Builder.method(HttpMethod.GET)));
            return ResponseBuilder.modify(response).removeEntity().build();
        }

        return decorated.handle(request);
    }

}
