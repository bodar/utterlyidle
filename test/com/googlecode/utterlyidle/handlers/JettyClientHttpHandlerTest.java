package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.Server;

import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static com.googlecode.utterlyidle.jetty.eclipse.RestServer.restServer;

public class JettyClientHttpHandlerTest extends AbstractTestClientHttpHandler {
    @Override
    protected Server server(Application application) throws Exception {
        return restServer(application, defaultConfiguration());
    }

    @Override
    protected ClientHttpHandler clientHttpHandler(final int timeout) {
        return new ClientHttpHandler(timeout);
    }
}
