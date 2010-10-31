package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Renderer;
import com.googlecode.utterlyidle.Response;
import com.googlecode.yadic.Resolver;

public class RendererHandler extends CompositeHandler<Renderer> {
    @Override
    public void process(Renderer renderer, Object result, Resolver resolver, Response response) throws Exception {
        response.write(renderer.render(result));
    }
}
