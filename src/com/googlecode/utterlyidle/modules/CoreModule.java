package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.handlers.Auditor;
import com.googlecode.utterlyidle.handlers.Auditors;
import com.googlecode.utterlyidle.handlers.ByteArrayHandler;
import com.googlecode.utterlyidle.handlers.NoContentHandler;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.handlers.ResponseHandlersFinder;
import com.googlecode.utterlyidle.handlers.StreamingOutputHandler;
import com.googlecode.utterlyidle.handlers.StreamingWriterHandler;
import com.googlecode.utterlyidle.rendering.ExceptionRenderer;
import com.googlecode.utterlyidle.rendering.MatchFailureRenderer;
import com.googlecode.utterlyidle.rendering.ObjectRenderer;
import com.googlecode.yadic.Container;

import static com.googlecode.totallylazy.Predicates.*;
import static com.googlecode.totallylazy.URLs.packageUrl;
import static com.googlecode.utterlyidle.dsl.DslBindings.bindings;
import static com.googlecode.utterlyidle.dsl.StaticBindingBuilder.in;
import static com.googlecode.utterlyidle.handlers.HandlerRule.entity;
import static com.googlecode.utterlyidle.handlers.RenderingResponseHandler.renderer;

public class CoreModule extends AbstractModule {
    @Override
    public Module defineModules(ModuleDefinitions moduleDefinitions) {
        moduleDefinitions.addApplicationModule(ApplicationScopedModule.class);
        moduleDefinitions.addApplicationModule(ResourcesModule.class);
        moduleDefinitions.addApplicationModule(ResponseHandlersModule.class);
        moduleDefinitions.addRequestModule(RequestScopedModule.class);
        moduleDefinitions.addRequestModule(AuditModule.class);
        moduleDefinitions.addArgumentModule(ArgumentScopedModule.class);
        return this;
    }

    @Override
    public Module addPerRequestObjects(Container container) {
        container.addActivator(ResourcePath.class, ResourcePathActivator.class);
        container.addActivator(BaseUri.class, BaseUriActivator.class);
        container.add(ResponseHandlersFinder.class);
        container.add(Auditors.class, Auditors.class);
        container.addActivator(Auditor.class, container.getActivator(Auditors.class));

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
        resources.add(bindings(in(packageUrl(Application.class)).path("utterlyidle")));
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