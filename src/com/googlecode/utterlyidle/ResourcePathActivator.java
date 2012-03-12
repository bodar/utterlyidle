package com.googlecode.utterlyidle;

import java.util.concurrent.Callable;

public class ResourcePathActivator implements Callable<ResourcePath> {
    private final Request request;

    public ResourcePathActivator(Request request) {
        this.request = request;
    }

    public ResourcePath call() throws Exception {
        return ResourcePath.resourcePathOf(request);
    }
}
