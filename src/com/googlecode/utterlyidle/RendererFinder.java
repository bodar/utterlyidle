package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.utterlyidle.handlers.Renderers;
import com.googlecode.yadic.Resolver;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Callables.second;
import static com.googlecode.totallylazy.Callers.call;
import static com.googlecode.totallylazy.Predicates.by;
import static com.googlecode.totallylazy.Predicates.matches;
import static com.googlecode.yadic.CreateCallable.create;

public class RendererFinder {
    private final Resolver resolver;
    private final Renderers renderers;

    public RendererFinder(Resolver resolver, Renderers renderers) {
        this.resolver = resolver;
        this.renderers = renderers;
    }

}
