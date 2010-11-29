package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.cookies.Cookies;
import com.googlecode.utterlyidle.handlers.ExceptionHandler;
import com.googlecode.utterlyidle.handlers.MatchFailureHandler;
import com.googlecode.utterlyidle.rendering.BuiltInResources;
import com.googlecode.utterlyidle.rendering.ExceptionRenderer;
import com.googlecode.utterlyidle.rendering.MatchFailureRenderer;
import com.googlecode.utterlyidle.handlers.NullHandler;
import com.googlecode.utterlyidle.rendering.ObjectRenderer;
import com.googlecode.utterlyidle.handlers.RedirectHandler;
import com.googlecode.utterlyidle.handlers.RendererHandler;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.handlers.StreamingOutputHandler;
import com.googlecode.utterlyidle.handlers.StreamingWriterHandler;
import com.googlecode.yadic.Container;

import javax.ws.rs.core.StreamingOutput;

import static com.googlecode.totallylazy.Predicates.aNull;
import static com.googlecode.totallylazy.Predicates.assignableTo;


public class CoreModule implements Module{
    public Module addPerRequestObjects(Container container) {
        final Request request = container.get(Request.class);
        container.add(Cookies.class);
        container.addInstance(BasePath.class, request.basePath());
        container.addInstance(ResourcePath.class, request.resourcePath());
        container.add(BuiltInResources.class);
        return this;
    }

    public Module addPerApplicationObjects(Container container) {
        container.add(Engine.class, RestEngine.class);
        final Engine engine = container.get(Engine.class);

        container.addInstance(ResponseHandlers.class, engine.responseHandlers());
        container.addInstance(RendererHandler.class, engine.renderers());
        return this;
    }

    public Module addResources(Engine engine) {
        final ResponseHandlers handlers = engine.responseHandlers();
        final RendererHandler renderers = engine.renderers();

        handlers.addGuard(aNull(Object.class), NullHandler.class);
        handlers.addGuard(assignableTo(SeeOther.class), RedirectHandler.class);
        handlers.addGuard(assignableTo(StreamingWriter.class), StreamingWriterHandler.class);
        handlers.addGuard(assignableTo(StreamingOutput.class), StreamingOutputHandler.class);
        handlers.addGuard(assignableTo(MatchFailure.class), new MatchFailureHandler(renderers));
        handlers.addCatchAll(assignableTo(Object.class), renderers);

        renderers.addCatchAll(assignableTo(MatchFailure.class), MatchFailureRenderer.class);
        renderers.addCatchAll(assignableTo(Exception.class), ExceptionRenderer.class);
        renderers.addCatchAll(assignableTo(Object.class), ObjectRenderer.class);

        engine.add(BuiltInResources.class);
        return this;
    }
}