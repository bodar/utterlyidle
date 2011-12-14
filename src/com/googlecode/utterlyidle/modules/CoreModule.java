package com.googlecode.utterlyidle.modules;

import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.SystemClock;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.cookies.CookieParameters;
import com.googlecode.utterlyidle.handlers.*;
import com.googlecode.utterlyidle.io.HierarchicalPath;
import com.googlecode.utterlyidle.rendering.ExceptionRenderer;
import com.googlecode.utterlyidle.rendering.MatchFailureRenderer;
import com.googlecode.utterlyidle.rendering.ObjectRenderer;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.generics.TypeFor;
import com.googlecode.yadic.resolvers.OptionResolver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import static com.googlecode.totallylazy.Predicates.*;
import static com.googlecode.totallylazy.URLs.packageUrl;
import static com.googlecode.utterlyidle.dsl.DslBindings.bindings;
import static com.googlecode.utterlyidle.dsl.StaticBindingBuilder.in;
import static com.googlecode.utterlyidle.handlers.HandlerRule.entity;
import static com.googlecode.utterlyidle.handlers.RenderingResponseHandler.renderer;
import static com.googlecode.utterlyidle.io.HierarchicalPath.hierarchicalPath;

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
        container.add(Redirector.class, BaseUriRedirector.class);
        container.add(RequestGenerator.class, BindingsRequestGenerator.class);
        container.add(ResponseHandlersFinder.class);
        container.add(Auditors.class, Auditors.class);
        container.addActivator(Auditor.class, container.getActivator(Auditors.class));
        container.add(HttpClient.class, ClientHttpHandler.class);
        return this;
    }

    @Override
    public Module addPerApplicationObjects(Container container) {
        container.add(Clock.class, SystemClock.class);
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

    @Override
    public Module addPerArgumentObjects(Container argumentScope) throws Exception {
        Request request = argumentScope.get(Request.class);
        argumentScope.addInstance(Uri.class, request.uri());
        argumentScope.addInstance(HierarchicalPath.class, hierarchicalPath(request.uri().path()));
        argumentScope.addActivator(PathParameters.class, PathParametersActivator.class);
        argumentScope.addInstance(HeaderParameters.class, request.headers());
        argumentScope.addInstance(QueryParameters.class, Requests.query(request));
        argumentScope.addInstance(FormParameters.class, Requests.form(request));
        argumentScope.addInstance(CookieParameters.class, Requests.cookies(request));
        argumentScope.addInstance(InputStream.class, new ByteArrayInputStream(request.entity()));
        argumentScope.add(new TypeFor<Option<?>>() {}.get(), new OptionResolver(argumentScope, instanceOf(IllegalArgumentException.class)));
        argumentScope.add(new TypeFor<Either<?, ?>>() {}.get(), new EitherResolver(argumentScope));
        argumentScope.addActivator(UUID.class, UUIDActivator.class);
        return this;
    }
}