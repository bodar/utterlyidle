package com.googlecode.utterlyidle.profiling;

import com.googlecode.lazyrecords.Logger;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.handlers.HttpClient;
import com.googlecode.utterlyidle.modules.ApplicationScopedModule;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.yadic.Container;

public class ProfilingModule implements RequestScopedModule, ApplicationScopedModule {
    @Override
    public Container addPerRequestObjects(Container container) throws Exception {
        container.addActivator(ProfilingData.class, ProfilingDataActivator.class);
        if (container.contains(Logger.class)) {
            container.decorate(Logger.class, ProfilingLogger.class);
        }
        container.decorate(HttpHandler.class, ProfilingHandler.class);
        container.decorate(HttpClient.class, ProfilingClient.class);
        return container;
    }

    @Override
    public Container addPerApplicationObjects(final Container container) throws Exception {
        if (container.contains(Logger.class)) {
            container.decorate(Logger.class, ProfilingLogger.class);
        }
        return container;
    }
}
