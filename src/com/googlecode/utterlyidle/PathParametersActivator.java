package com.googlecode.utterlyidle;

import java.util.concurrent.Callable;

public class PathParametersActivator implements Callable<PathParameters> {
    private final Request request;
    private final UriTemplate uriTemplate;

    public PathParametersActivator(Request request, UriTemplate uriTemplate) {
        this.request = request;
        this.uriTemplate = uriTemplate;
    }

    public PathParameters call() throws Exception {
        return uriTemplate.extract(request.uri().path());
    }
}
