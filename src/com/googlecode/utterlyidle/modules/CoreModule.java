package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.handlers.*;
import com.googlecode.utterlyidle.rendering.BuiltInResources;
import com.googlecode.utterlyidle.rendering.ExceptionRenderer;
import com.googlecode.utterlyidle.rendering.MatchFailureRenderer;
import com.googlecode.utterlyidle.rendering.ObjectRenderer;
import com.googlecode.yadic.Container;

import javax.ws.rs.core.StreamingOutput;

import static com.googlecode.totallylazy.Predicates.*;
import static com.googlecode.utterlyidle.handlers.HandlerRule.entity;
import static com.googlecode.utterlyidle.handlers.RenderingResponseHandler.renderer;

public class CoreModule extends AbstractModule {
    @Override
    public Module addPerRequestObjects(Container container) {
        container.addActivator(ResourcePath.class, ResourcePathActivator.class);
        container.add(BuiltInResources.class);
        container.add(ResponseHandlersFinder.class);
        return this;
    }

    @Override
    public Module addPerApplicationObjects(Container container) {
        container.add(Resources.class, RegisteredResources.class);
        container.addActivator(Bindings.class, container.getActivator(Resources.class));
        container.add(ResponseHandlers.class);
        return this;
    }

    @Override
    public Module addResources(Resources resources) {
        resources.add(BuiltInResources.class);
        return this;
    }

    @Override
    public Module addResponseHandlers(ResponseHandlers handlers){
        handlers.addGuard(where(entity(), is(nullValue())), NoContentHandler.class);
        handlers.addGuard(where(entity(), is(instanceOf(byte[].class))), ByteArrayHandler.class);
        handlers.addGuard(where(entity(), is(instanceOf(StreamingWriter.class))), StreamingWriterHandler.class);
        handlers.addGuard(where(entity(), is(instanceOf(StreamingOutput.class))), StreamingOutputHandler.class);
        handlers.addCatchAll(where(entity(), is(instanceOf(MatchFailure.class))), renderer(MatchFailureRenderer.class));
        handlers.addCatchAll(where(entity(), is(instanceOf(Exception.class))), renderer(ExceptionRenderer.class));
        handlers.addCatchAll(where(entity(), is(instanceOf(Object.class))), renderer(ObjectRenderer.class));
        return this;
    }
}