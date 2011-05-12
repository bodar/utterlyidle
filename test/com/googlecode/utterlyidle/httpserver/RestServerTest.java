package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.CloseableCallable;
import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.ServerContract;

public class RestServerTest extends ServerContract {
    protected Server createServer(CloseableCallable<Application> activator) throws Exception {
        return new RestServer(activator);
    }

}
