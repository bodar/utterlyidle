package com.googlecode.utterlyidle;

import java.util.concurrent.Callable;

public class BasePathActivator implements Callable<BasePath> {
    private final ServerUrl serverUrl;

    public BasePathActivator(ServerUrl serverUrl) {
        this.serverUrl = serverUrl;
    }

    public BasePath call() throws Exception {
        return new BasePath(serverUrl.path().toString());
    }
}
