package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.RendererFinder;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;

import java.io.OutputStreamWriter;
import java.io.Writer;

public class RenderingResponseHandler implements ResponseHandler<Object>{
    private final RendererFinder renderers;

    public RenderingResponseHandler(RendererFinder renderers) {
        this.renderers = renderers;
    }

    public void handle(Response response) throws Exception {
        Writer writer = new OutputStreamWriter(response.output());
        writer.write(renderers.findRenderer(null, response).render(response.entity()));
        writer.flush();
    }
}
