package com.googlecode.utterlyidle;

import com.googlecode.yadic.SimpleContainer;

import java.util.concurrent.Callable;

public class ServerActivator implements Callable<Server> {
    private final ServerConfiguration serverConfiguration;
    private final Application application;

    public ServerActivator(ServerConfiguration serverConfiguration, Application application) {
        this.serverConfiguration = serverConfiguration;
        this.application = application;
    }

    public Server call() throws Exception {
        return new SimpleContainer().
            addInstance(Application.class, application).
            addInstance(ServerConfiguration.class, serverConfiguration).
            add(serverConfiguration.serverClass()).
            get(serverConfiguration.serverClass());
    }
}
