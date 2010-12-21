package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Renderer;
import com.googlecode.utterlyidle.Response;

import java.io.OutputStreamWriter;

public class RendererHandler extends HandlerRules<Renderer> {
    @Override
    public void process(Renderer renderer, Response response) throws Exception {
        OutputStreamWriter writer = new OutputStreamWriter(response.output());
        writer.write(renderer.render(response.entity()));
        writer.close();
    }
}
