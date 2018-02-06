package com.googlecode.utterlyidle.jetty;

import com.googlecode.utterlyidle.ServerContract;

public class RestServerTest extends ServerContract<RestServer> {
    @Override
    protected Class<RestServer> server() {
        return RestServer.class;
    }

    @Override
    protected boolean isServletBased() {
        return true;
    }
}
