package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestHandler;
import com.googlecode.utterlyidle.Response;

public class SiteMeshHandler implements RequestHandler {
    private final RequestHandler requestHandler;

    public SiteMeshHandler(RequestHandler requestHandler ) {
        this.requestHandler = requestHandler;
    }

    public void handle(Request request, Response response) throws Exception {
        requestHandler.handle(request, response.output(new SiteMeshOutputStream(response.output())));
    }

}