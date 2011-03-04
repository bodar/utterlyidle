package com.googlecode.utterlyidle;

import java.util.concurrent.Callable;

public class ResourcePathActivator implements Callable<ResourcePath> {
    private final BasePath basePath;
    private final Request request;

    public ResourcePathActivator(BasePath basePath, Request request) {
        this.basePath = basePath;
        this.request = request;
    }

    public ResourcePath call() throws Exception {
        return ResourcePath.resourcePathOf(request, basePath);
    }
}
