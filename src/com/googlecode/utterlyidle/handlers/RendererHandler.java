package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Predicates;
import com.googlecode.utterlyidle.Renderer;
import com.googlecode.utterlyidle.Response;
import com.googlecode.yadic.Resolver;

import java.io.IOException;

public class RendererHandler extends CompositeHandler<Renderer> {
    @Override
    public void process(Renderer renderer, Object result, Resolver resolver, Response response) throws IOException {
        response.write(renderer.render(result));
    }
}
