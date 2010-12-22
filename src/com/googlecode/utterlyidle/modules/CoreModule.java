package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.*;
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
    @Override
    public Module addPerRequestObjects(Container container) {
        container.add(Cookies.class);
        container.addActivator(BasePath.class, BasePathActivator.class);
        container.addActivator(ResourcePath.class, ResourcePathActivator.class);
        container.add(BuiltInResources.class);
        container.add(ResponseHandlersHandler.class);
        return this;
    }

    @Override
    public Module addPerApplicationObjects(Container container) {
        container.add(Resources.class, RestEngine.class);
        container.addActivator(ActivatorFinder.class, container.getActivator(Resources.class));
        container.add(ResponseHandlers.class);
        container.add(Renderers.class);
        return this;
    }

    @Override
    public Module addResources(Resources resources) {
        resources.add(BuiltInResources.class);
        return this;
    }

    @Override
    public Module addResponseHandlers(ResponseHandlers handlers){
        handlers.addGuard(where(entity(), is(aNull(Object.class))), NullHandler.class);
        handlers.addGuard(where(entity(), is(instanceOf(SeeOther.class))), RedirectHandler.class);
        handlers.addGuard(where(entity(), is(instanceOf(StreamingWriter.class))), StreamingWriterHandler.class);
        handlers.addGuard(where(entity(), is(instanceOf(StreamingOutput.class))), StreamingOutputHandler.class);
        handlers.addCatchAll(where(entity(), is(instanceOf(Object.class))), RenderingResponseHandler.class);
        return this;
    }

    @Override
    public Module addRenderers(Renderers renderers){
        renderers.addCatchAll(where(entity(), is(instanceOf(MatchFailure.class))), MatchFailureRenderer.class);
        renderers.addCatchAll(where(entity(), is(instanceOf(Exception.class))), ExceptionRenderer.class);
        renderers.addCatchAll(where(entity(), is(instanceOf(Object.class))), ObjectRenderer.class);
        return this;
    }
}