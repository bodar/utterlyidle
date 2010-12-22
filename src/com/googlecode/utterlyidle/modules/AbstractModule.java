package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.handlers.Renderers;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.yadic.Container;

public abstract class AbstractModule implements
        ApplicationScopedModule, RequestScopedModule, ResourcesModule, ResponseHandlersModule, RenderersModule {
    public Module addPerRequestObjects(Container container) {
        return this;
    }

    public Module addPerApplicationObjects(Container container) {
        return this;
    }

    public Module addResources(Resources resources) {
        return this;
    }

    public Module addResponseHandlers(ResponseHandlers handlers) {
        return this;
    }

    public Module addRenderers(Renderers renderers) {
        return this;
    }
}
