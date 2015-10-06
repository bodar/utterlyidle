package com.googlecode.utterlyidle.undertow;

import com.googlecode.utterlyidle.ServerContract;

public class RestServerTest extends ServerContract<RestServer> {
    @Override
    protected Class<RestServer> server() throws Exception {
        return RestServer.class;
    }
}
