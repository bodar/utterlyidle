package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.utterlyidle.*;
import com.googlecode.yadic.Resolver;

import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Callables.second;
import static com.googlecode.totallylazy.Callers.call;
import static com.googlecode.totallylazy.Predicates.by;
import static com.googlecode.totallylazy.Predicates.matches;
import static com.googlecode.yadic.CreateCallable.create;

public class RenderingResponseHandler implements ResponseHandler {
    private final Renderers renderers;
    private final Resolver resolver;

    public RenderingResponseHandler(Renderers renderers, Resolver resolver) {
        this.renderers = renderers;
        this.resolver = resolver;
    }

    public void handle(Request request, Response response) throws Exception {
        Writer writer = new OutputStreamWriter(response.output());
        writer.write(findRenderer(request, response).render(response.entity()));
        writer.flush();
    }

    @SuppressWarnings("unchecked")
    private Renderer findRenderer(Request request, Response response){
        final Object handler = renderers.handlers().filter(by((Callable1) first(), matches(response.entity()))).map(second()).head();
        if (handler instanceof Class) {
            return (Renderer) call(create((Class) handler, resolver));
        }
        return (Renderer) handler;
    }

}
