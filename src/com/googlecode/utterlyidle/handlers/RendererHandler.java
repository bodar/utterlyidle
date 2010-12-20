package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.MemoryResponse;
import com.googlecode.utterlyidle.Renderer;
import com.googlecode.utterlyidle.Response;
import com.googlecode.yadic.Resolver;

import java.io.OutputStreamWriter;

public class RendererHandler extends CompositeHandler<Renderer> {
    @Override
    public void process(Renderer renderer, Object result, Resolver resolver, Response response) throws Exception {
        OutputStreamWriter writer = new OutputStreamWriter(response.output());
        writer.write(renderer.render(result));
        writer.close();
    }
}
