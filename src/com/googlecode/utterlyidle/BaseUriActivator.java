package com.googlecode.utterlyidle;

import java.util.concurrent.Callable;

public class BaseUriActivator implements Callable<BaseUri>{
    private final Request request;
    private final BasePath basePath;

    public BaseUriActivator(Request request, BasePath basePath) {
        this.request = request;
        this.basePath = basePath;
    }

    @Override
    public BaseUri call() throws Exception {
        return BaseUri.baseUri(request, basePath);
    }

}
