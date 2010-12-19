package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestHandler;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.totallylazy.Sequences.sequence;

public class SiteMeshHandler implements RequestHandler {
    private final RequestHandler requestHandler;
    private final Decorators decorators;

    public SiteMeshHandler(final RequestHandler requestHandler, final Decorators decorators) {
        this.requestHandler = requestHandler;
        this.decorators = decorators;
    }

    public void handle(final Request request, final Response response) throws Exception {
        requestHandler.handle(request, new SiteMeshResponse(request, response, decorators));
    }
}