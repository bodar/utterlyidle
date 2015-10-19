package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.Server;

import static com.googlecode.utterlyidle.ApplicationBuilder.application;

public class DefaultClientHttpHandlerTest extends AbstractTestClientHttpHandler {

    @Override
    protected Server server(Application application) {
        return application(application).start();
    }

    @Override
    protected ClientHttpHandler clientHttpHandler(final int timeout) {
        return new ClientHttpHandler(timeout);
    }
}
