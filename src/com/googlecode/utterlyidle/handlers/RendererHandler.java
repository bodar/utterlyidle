package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Predicates;
import com.googlecode.utterlyidle.Renderer;
import com.googlecode.utterlyidle.Response;
import com.googlecode.yadic.Resolver;

import java.io.IOException;

public class RendererHandler extends CompositeHandler<Renderer> {
    public RendererHandler() {
        super();
        addCatchAll(Predicates.assignableTo(Object.class), new ObjectRenderer());
    }

    @Override
    public void process(Renderer handler, Object result, Resolver resolver, Response response) throws IOException {
        response.write(handler.render(result));
    }
}
