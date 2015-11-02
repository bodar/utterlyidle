package com.googlecode.utterlyidle.modules;

import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.totallylazy.time.SystemClock;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.BaseUri;
import com.googlecode.utterlyidle.BaseUriActivator;
import com.googlecode.utterlyidle.BaseUriRedirector;
import com.googlecode.utterlyidle.Bindings;
import com.googlecode.utterlyidle.BindingsRequestGenerator;
import com.googlecode.utterlyidle.DebuggingOnlyExceptionLogger;
import com.googlecode.utterlyidle.EitherResolver;
import com.googlecode.utterlyidle.Entity;
import com.googlecode.utterlyidle.ExceptionLogger;
import com.googlecode.utterlyidle.FormParameters;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.MatchFailure;
import com.googlecode.utterlyidle.PathParameters;
import com.googlecode.utterlyidle.PathParametersActivator;
import com.googlecode.utterlyidle.QueryParameters;
import com.googlecode.utterlyidle.Redirector;
import com.googlecode.utterlyidle.RegisteredResources;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestGenerator;
import com.googlecode.utterlyidle.Requests;
import com.googlecode.utterlyidle.ResourcePath;
import com.googlecode.utterlyidle.ResourcePathActivator;
import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.SmartHttpClient;
import com.googlecode.utterlyidle.StreamingOutput;
import com.googlecode.utterlyidle.StreamingWriter;
import com.googlecode.utterlyidle.UUIDActivator;
import com.googlecode.utterlyidle.UriActivator;
import com.googlecode.utterlyidle.annotations.View;
import com.googlecode.utterlyidle.cookies.CookieParameters;
import com.googlecode.utterlyidle.handlers.ApplicationId;
import com.googlecode.utterlyidle.handlers.Auditor;
import com.googlecode.utterlyidle.handlers.Auditors;
import com.googlecode.utterlyidle.handlers.ClientHttpHandler;
import com.googlecode.utterlyidle.handlers.HttpClient;
import com.googlecode.utterlyidle.handlers.IdentityHandler;
import com.googlecode.utterlyidle.handlers.InternalHttpHandler;
import com.googlecode.utterlyidle.handlers.InternalInvocationHandler;
import com.googlecode.utterlyidle.handlers.InvocationHandler;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.handlers.ResponseHandlersFinder;
import com.googlecode.utterlyidle.rendering.ExceptionRenderer;
import com.googlecode.utterlyidle.rendering.MatchFailureRenderer;
import com.googlecode.utterlyidle.rendering.ObjectRenderer;
import com.googlecode.utterlyidle.rendering.SeeOtherRenderer;
import com.googlecode.utterlyidle.rendering.ViewConvention;
import com.googlecode.utterlyidle.services.Services;
import com.googlecode.utterlyidle.services.ServicesModule;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;
import com.googlecode.yadic.generics.TypeFor;
import com.googlecode.yadic.resolvers.OptionResolver;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Unchecked.cast;
import static com.googlecode.totallylazy.io.URLs.packageUrl;
import static com.googlecode.totallylazy.predicates.Predicates.instanceOf;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.totallylazy.predicates.Predicates.where;
import static com.googlecode.utterlyidle.Status.SEE_OTHER;
import static com.googlecode.utterlyidle.dsl.DslBindings.bindings;
import static com.googlecode.utterlyidle.dsl.StaticBindingBuilder.in;
import static com.googlecode.utterlyidle.handlers.HandlerRule.entity;
import static com.googlecode.utterlyidle.handlers.HandlerRule.status;
import static com.googlecode.utterlyidle.handlers.RenderingResponseHandler.renderer;

public class CoreModule implements ModuleDefiner, RequestScopedModule, ApplicationScopedModule, ResourcesModule, ResponseHandlersModule, ArgumentScopedModule {
    @Override
    public ModuleDefinitions defineModules(ModuleDefinitions moduleDefinitions) {
        return moduleDefinitions.
                addApplicationModule(ApplicationScopedModule.class).
                addApplicationModule(ResourcesModule.class).
                addApplicationModule(ResponseHandlersModule.class).
                addApplicationModule(ServicesModule.class).
                addRequestModule(RequestScopedModule.class).
                addRequestModule(AuditModule.class).
                addArgumentModule(ArgumentScopedModule.class);
    }

    @Override
    public Container addPerRequestObjects(Container container) {
        return container.
                addActivator(View.class, ViewConvention.class).
                addActivator(ResourcePath.class, ResourcePathActivator.class).
                addActivator(BaseUri.class, BaseUriActivator.class).
                add(Redirector.class, BaseUriRedirector.class).
                add(RequestGenerator.class, BindingsRequestGenerator.class).
                add(ResponseHandlersFinder.class).
                add(Auditors.class, Auditors.class).
                addActivator(Auditor.class, container.getActivator(Auditors.class)).
                add(HttpClient.class, ClientHttpHandler.class).
                add(InternalHttpHandler.class).
                decorate(HttpClient.class, SmartHttpClient.class).
                add(InvocationHandler.class, InternalInvocationHandler.class).
                add(ExceptionLogger.class, DebuggingOnlyExceptionLogger.class);
    }

    @Override
    public Container addPerApplicationObjects(Container container) {
        return container.add(Clock.class, SystemClock.class).
                add(Resources.class, RegisteredResources.class).
                addActivator(Bindings.class, container.getActivator(Resources.class)).
                add(ResponseHandlers.class).
                add(ApplicationId.class).
                add(InternalRequestMarker.class).
                add(Services.class);
    }

    @Override
    public Resources addResources(Resources resources) {
        return resources.add(bindings(in(packageUrl(Application.class)).path("utterlyidle")));
    }

    @Override
    public ResponseHandlers addResponseHandlers(ResponseHandlers handlers) {
        return handlers.
                addGuard(where(entity(), is(instanceOf(byte[].class)).or(instanceOf(StreamingWriter.class)).
                        or(instanceOf(StreamingOutput.class)).or(instanceOf(InputStream.class))), IdentityHandler.class).
                addCatchAll(where(status(), is(SEE_OTHER)).and(where(entity(), is(instanceOf(String.class)))), renderer(SeeOtherRenderer.class)).
                addCatchAll(where(entity(), is(instanceOf(MatchFailure.class))), renderer(MatchFailureRenderer.class)).
                addCatchAll(where(entity(), is(instanceOf(Exception.class))), renderer(ExceptionRenderer.class)).
                addCatchAll(where(entity(), is(instanceOf(Object.class))), renderer(ObjectRenderer.class));
    }

    @Override
    public Container addPerArgumentObjects(Container argumentScope) throws Exception {
        Request request = argumentScope.get(Request.class);
        return (Container) argumentScope.addActivator(PathParameters.class, PathParametersActivator.class).
                addInstance(HeaderParameters.class, request.headers()).
                addInstance(QueryParameters.class, request.query()).
                addInstance(FormParameters.class, Requests.form(request)).
                addInstance(CookieParameters.class, Requests.cookies(request)).
                addInstance(Entity.class, request.entity()).
                addInstance(InputStream.class, request.entity().inputStream()).
                addActivator(UUID.class, UUIDActivator.class).
                addActivator(Date.class, DateActivator.class).
                addActivator(Uri.class, UriActivator.class).
                addType(new TypeFor<Option<?>>() {
                }.get(), new OptionResolver(argumentScope, instanceOf(IllegalArgumentException.class))).
                addType(new TypeFor<Either<?, ?>>() {
                }.get(), EitherResolver.class).
                addType(new TypeFor<Iterable<UUID>>() {
                }.get(), new TypedIterableResolver<UUID>(argumentScope, UUIDActivator.fromString()));
    }

    public static class TypedIterableResolver<T> implements Resolver<Iterable<T>> {
        private final Container container;
        private final Function1<String, T> mapper;

        public TypedIterableResolver(Container container, Function1<String, T> mapper) {
            this.container = container;
            this.mapper = mapper;
        }

        @Override
        public Iterable<T> resolve(Type type) throws Exception {
            Iterable<String> values = cast(container.resolve(new TypeFor<Iterable<String>>() {
            }.get()));
            return sequence(values).map(mapper).realise();
        }
    }

    public static class DateActivator implements Callable<Date> {
        private final String date;

        public DateActivator(String date) {
            this.date = date;
        }

        @Override
        public Date call() throws Exception {
            return Dates.parse(date);
        }
    }
}