package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

public class SiteMeshHandler implements HttpHandler {
    private final HttpHandler httpHandler;
    private final Decorators decorators;

    public SiteMeshHandler(final HttpHandler httpHandler, final Decorators decorators) {
        this.httpHandler = httpHandler;
        this.decorators = decorators;
    }

    public void handle(final Request request, final Response response) throws Exception {
        httpHandler.handle(request, new SiteMeshResponse(request, response, decorators));
    }
}