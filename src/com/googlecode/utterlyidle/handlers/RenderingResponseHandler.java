package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Renderer;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.modules.DependsOnResolver;
import com.googlecode.yadic.Resolver;

import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.googlecode.totallylazy.Callers.call;
import static com.googlecode.yadic.CreateCallable.create;

public class RenderingResponseHandler<T> implements ResponseHandler, DependsOnResolver {
    private final Class<? extends Renderer<T>> renderer;
    private Resolver resolver;

    private RenderingResponseHandler(Class<? extends Renderer<T>> renderer) {
        this.renderer = renderer;
    }

    public static <T> RenderingResponseHandler<T> renderer(Class<? extends Renderer<T>> renderer) {
        return new RenderingResponseHandler<T>(renderer);
    }

    public void handle(Request request, Response response) throws Exception {
        Writer writer = new OutputStreamWriter(response.output());
        writer.write(call(create(renderer, resolver)).render((T) response.entity()));
        writer.flush();
    }

    public void setResolver(Resolver resolver) {
        this.resolver = resolver;
    }
}
