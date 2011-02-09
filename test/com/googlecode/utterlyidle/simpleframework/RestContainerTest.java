package com.googlecode.utterlyidle.simpleframework;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.ServerContract;
import org.junit.AfterClass;

public class RestContainerTest extends ServerContract {
    private static RestServer server;

    protected void ensureServerIsStarted(Application application) throws Exception {
        if(server!=null)return;
        server = new RestServer(port(), application);
    }

    @AfterClass
    public static void stopServer() throws Exception {
        server.stop();
    }

    protected int port() {
        return 8000;
    }
}
