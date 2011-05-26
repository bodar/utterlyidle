package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.ServerContract;

public class RestServerTest extends ServerContract {
    @Override
    protected Class<? extends Server> server() throws Exception {
        return RestServer.class;
    }
}
