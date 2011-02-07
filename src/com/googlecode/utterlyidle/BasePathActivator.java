package com.googlecode.utterlyidle;

import java.util.concurrent.Callable;

public class BasePathActivator implements Callable<BasePath> {
    private final Request request;

    public BasePathActivator(Request request) {
        this.request = request;
    }

    public BasePath call() throws Exception {
        return request.basePath();
    }
}
