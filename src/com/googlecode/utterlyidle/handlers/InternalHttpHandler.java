package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

public class InternalHttpHandler implements HttpHandler {
    private final InternalRequestMarker marker;
    private final Application application;

    public InternalHttpHandler(InternalRequestMarker marker, Application application) {
        this.marker = marker;
        this.application = application;
    }

    @Override
    public Response handle(Request request) throws Exception {
        return application.handle(marker.markAsInternal(request));
    }
}
