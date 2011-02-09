package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.Application;
import static com.googlecode.utterlyidle.BasePath.basePath;
import com.googlecode.utterlyidle.ServerContract;

public class RestServerTest extends ServerContract {
    private static RestServer server;

    protected void ensureServerIsStarted(Application application) throws Exception {
        if(server!=null)return;
        server = new RestServer(port(), basePath("/"), application);
    }

    protected int port() {
        return 8001;
    }
}
