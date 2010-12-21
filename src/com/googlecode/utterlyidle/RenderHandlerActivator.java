package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.handlers.Renderers;
import com.googlecode.yadic.Resolver;

public class RenderHandlerActivator implements ResponseHandler<Object>{
    private final Resolver resolver;
    private final Renderers renderers;

    public RenderHandlerActivator(Resolver resolver, Renderers renderers) {
        this.resolver = resolver;
        this.renderers = renderers;
    }

    public void handle(Response response) throws Exception {
        renderers.with(resolver).handle(response);
    }
}
