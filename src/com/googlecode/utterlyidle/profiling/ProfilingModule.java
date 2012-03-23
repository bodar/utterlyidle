package com.googlecode.utterlyidle.profiling;

import com.googlecode.lazyrecords.Loggers;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.handlers.HttpClient;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.yadic.Container;

public class ProfilingModule implements RequestScopedModule {
    @Override
    public Module addPerRequestObjects(Container container) throws Exception {
        ProfilingData data = new ProfilingData();
        if (container.contains(Loggers.class)) {
            container.get(Loggers.class).add(data);
        }
        container.decorate(HttpHandler.class, ProfilingHandler.class);
        container.decorate(HttpClient.class, ProfilingClient.class);
        container.addInstance(ProfilingData.class, data);
        return this;
    }
}
