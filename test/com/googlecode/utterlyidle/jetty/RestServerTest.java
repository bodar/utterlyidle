package com.googlecode.utterlyidle.jetty;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.ServerContract;

import static com.googlecode.utterlyidle.BasePath.basePath;

public class RestServerTest extends ServerContract {
    private static Server server;

    protected void ensureServerIsStarted(Application application) throws Exception {
        if(server!=null)return;
        server = new RestServer(port(), basePath("/"), application);
    }

    protected int port() {
        return 8002;
    }
}
