package com.googlecode.utterlyidle.jetty;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.CloseableCallable;
import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.ServerContract;

import static com.googlecode.utterlyidle.BasePath.basePath;

public class RestServerTest extends ServerContract {
    protected Server createServer(final CloseableCallable<Application> activator) throws Exception {
        return new RestServer(basePath("/"), activator);
    }

}
