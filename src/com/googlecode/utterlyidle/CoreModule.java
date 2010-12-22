package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.cookies.Cookies;
import com.googlecode.utterlyidle.handlers.*;
import com.googlecode.utterlyidle.rendering.BuiltInResources;
import com.googlecode.utterlyidle.rendering.ExceptionRenderer;
import com.googlecode.utterlyidle.rendering.MatchFailureRenderer;
import com.googlecode.utterlyidle.rendering.ObjectRenderer;
import com.googlecode.yadic.Container;

import javax.ws.rs.core.StreamingOutput;

import static com.googlecode.totallylazy.Predicates.aNull;
import static com.googlecode.totallylazy.Predicates.assignableTo;

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

        responseHandlerRegistry.addGuard(aNull(Object.class), NullHandler.class);
        responseHandlerRegistry.addGuard(assignableTo(SeeOther.class), RedirectHandler.class);
        responseHandlerRegistry.addGuard(assignableTo(StreamingWriter.class), StreamingWriterHandler.class);
        responseHandlerRegistry.addGuard(assignableTo(StreamingOutput.class), StreamingOutputHandler.class);
        responseHandlerRegistry.addCatchAll(assignableTo(Object.class), RenderingResponseHandler.class);

        renderers.addCatchAll(assignableTo(MatchFailure.class), MatchFailureRenderer.class);
        renderers.addCatchAll(assignableTo(Exception.class), ExceptionRenderer.class);
        renderers.addCatchAll(assignableTo(Object.class), ObjectRenderer.class);

        engine.add(BuiltInResources.class);
        return this;
    }
}