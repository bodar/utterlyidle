package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.handlers.ExceptionHandler;
import com.googlecode.utterlyidle.handlers.MatchFailureHandler;
import com.googlecode.utterlyidle.handlers.MatchFailureRenderer;
import com.googlecode.utterlyidle.handlers.NullHandler;
import com.googlecode.utterlyidle.handlers.ObjectRenderer;
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
        return this;
    }

    public Module addPerApplicationObjects(Container container) {
        container.add(Engine.class, RestEngine.class);
        return this;
    }

    public Module addResources(Engine engine) {
        final ResponseHandlers handlers = engine.responseHandlers();
        final RendererHandler renderers = engine.renderers();

        handlers.addGuard(aNull(Object.class), NullHandler.class);
        handlers.addGuard(assignableTo(Redirect.class), RedirectHandler.class);
        handlers.addGuard(assignableTo(StreamingWriter.class), StreamingWriterHandler.class);
        handlers.addGuard(assignableTo(StreamingOutput.class), StreamingOutputHandler.class);
        handlers.addGuard(assignableTo(MatchFailure.class), new MatchFailureHandler(renderers));
        handlers.addCatchAll(assignableTo(UnsupportedOperationException.class), new ExceptionHandler(Status.NOT_IMPLEMENTED, renderers));
        handlers.addCatchAll(assignableTo(Exception.class), new ExceptionHandler(Status.INTERNAL_SERVER_ERROR, renderers));
        handlers.addCatchAll(assignableTo(Object.class), renderers);

        renderers.addCatchAll(assignableTo(MatchFailure.class), MatchFailureRenderer.class);
        renderers.addCatchAll(assignableTo(Object.class), ObjectRenderer.class);
        return this;
    }
}