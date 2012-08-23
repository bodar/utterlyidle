package com.googlecode.utterlyidle.profiling;

import com.googlecode.lazyrecords.Logger;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.handlers.HttpClient;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.yadic.Container;

import static com.googlecode.totallylazy.Function.returns;
import static com.googlecode.totallylazy.callables.LazyCallable.lazy;

public class ProfilingModule implements RequestScopedModule {
    @Override
    public Module addPerRequestObjects(Container container) throws Exception {
        container.addActivator(ProfilingData.class, ProfilingDataActivator.class);
        if (container.contains(Logger.class)) {
            container.decorate(Logger.class, ProfilingLogger.class);
        }
        container.decorate(HttpHandler.class, ProfilingHandler.class);
        container.decorate(HttpClient.class, ProfilingClient.class);
        return this;
    }
}
