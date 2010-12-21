package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.handlers.RendererHandler;
import com.googlecode.yadic.Resolver;

public class RenderHandlerActivator implements ResponseHandler<Object>{
    private final Resolver resolver;
    private final RendererHandler rendererHandler;

    public RenderHandlerActivator(Resolver resolver, RendererHandler rendererHandler) {
        this.resolver = resolver;
        this.rendererHandler = rendererHandler;
    }

    public void handle(Response response) throws Exception {
        rendererHandler.with(resolver).handle(response);
    }
}
