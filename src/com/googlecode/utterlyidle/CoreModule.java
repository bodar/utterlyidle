package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.utterlyidle.cookies.Cookies;
import com.googlecode.utterlyidle.handlers.*;
import com.googlecode.utterlyidle.rendering.BuiltInResources;
import com.googlecode.utterlyidle.rendering.ExceptionRenderer;
import com.googlecode.utterlyidle.rendering.MatchFailureRenderer;
import com.googlecode.utterlyidle.rendering.ObjectRenderer;
import com.googlecode.yadic.Container;

import javax.ws.rs.core.StreamingOutput;

import static com.googlecode.totallylazy.Predicates.*;
import static com.googlecode.utterlyidle.handlers.HandlerRule.entity;

public class CoreModule extends AbstractModule {
    public Module addPerRequestObjects(Container container) {
        container.add(Cookies.class);
        container.addActivator(BasePath.class, BasePathActivator.class);
        container.addActivator(ResourcePath.class, ResourcePathActivator.class);
        container.add(BuiltInResources.class);
        container.add(ResponseHandlers.class);
        return this;
    }

    public Module addPerApplicationObjects(Container container) {
        container.add(Engine.class, RestEngine.class);
        final Engine engine = container.get(Engine.class);

        container.addInstance(ActivatorFinder.class, engine);
        container.addInstance(ResponseHandlerRegistry.class, engine.responseHandlers());
        container.addInstance(Renderers.class, engine.renderers());
        return this;
    }

    public Module addResources(Engine engine) {
        final ResponseHandlerRegistry responseHandlerRegistry = engine.responseHandlers();
        final Renderers renderers = engine.renderers();

        responseHandlerRegistry.addGuard(where(entity(), is(aNull(Object.class))), NullHandler.class);
        responseHandlerRegistry.addGuard(where(entity(), is(instanceOf(SeeOther.class))), RedirectHandler.class);
        responseHandlerRegistry.addGuard(where(entity(), is(instanceOf(StreamingWriter.class))), StreamingWriterHandler.class);
        responseHandlerRegistry.addGuard(where(entity(), is(instanceOf(StreamingOutput.class))), StreamingOutputHandler.class);
        responseHandlerRegistry.addCatchAll(where(entity(), is(instanceOf(Object.class))), RenderingResponseHandler.class);

        renderers.addCatchAll(where(entity(), is(instanceOf(MatchFailure.class))), MatchFailureRenderer.class);
        renderers.addCatchAll(where(entity(), is(instanceOf(Exception.class))), ExceptionRenderer.class);
        renderers.addCatchAll(where(entity(), is(instanceOf(Object.class))), ObjectRenderer.class);

        engine.add(BuiltInResources.class);
        return this;
    }



}