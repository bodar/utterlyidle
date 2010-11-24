package com.googlecode.utterlyidle;

import com.googlecode.yadic.Container;

public class RestRequestHandler implements RequestHandler {
    private final Container container;

    public RestRequestHandler(Container container) {
        this.container = container;
    }

    public void handle(Request request, Response response) {
        container.get(Engine.class).handle(container, request, response);
    }
}
