package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.BasePathActivator;
import com.googlecode.utterlyidle.Bindings;
import com.googlecode.utterlyidle.MatchFailure;
import com.googlecode.utterlyidle.RegisteredResources;
import com.googlecode.utterlyidle.ResourcePath;
import com.googlecode.utterlyidle.ResourcePathActivator;
import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.StreamingOutput;
import com.googlecode.utterlyidle.StreamingWriter;
import com.googlecode.utterlyidle.handlers.Auditor;
import com.googlecode.utterlyidle.handlers.ByteArrayHandler;
import com.googlecode.utterlyidle.handlers.DoNothingAuditor;
import com.googlecode.utterlyidle.handlers.NoContentHandler;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.handlers.ResponseHandlersFinder;
import com.googlecode.utterlyidle.handlers.StreamingOutputHandler;
import com.googlecode.utterlyidle.handlers.StreamingWriterHandler;
import com.googlecode.utterlyidle.rendering.BuiltInResources;
import com.googlecode.utterlyidle.rendering.ExceptionRenderer;
import com.googlecode.utterlyidle.rendering.MatchFailureRenderer;
import com.googlecode.utterlyidle.rendering.ObjectRenderer;
import com.googlecode.yadic.Container;

import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.nullValue;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.utterlyidle.handlers.HandlerRule.entity;
import static com.googlecode.utterlyidle.handlers.RenderingResponseHandler.renderer;

public class CoreModule extends AbstractModule {
    @Override
    public Module defineModules(ModuleDefinitions moduleDefinitions) {
        moduleDefinitions.addApplicationModule(ApplicationScopedModule.class);
        moduleDefinitions.addApplicationModule(ResourcesModule.class);
        moduleDefinitions.addApplicationModule(ResponseHandlersModule.class);
        moduleDefinitions.addRequestModule(RequestScopedModule.class);
        moduleDefinitions.addArgumentModule(ArgumentScopedModule.class);
        return this;
    }

    @Override
    public Module addPerRequestObjects(Container container) {
        container.addActivator(ResourcePath.class, ResourcePathActivator.class);
        container.addActivator(BasePath.class, BasePathActivator.class);
        container.add(BuiltInResources.class);
        container.add(ResponseHandlersFinder.class);
        container.add(Auditor.class, DoNothingAuditor.class);

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
    public Module addResponseHandlers(ResponseHandlers handlers) {
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