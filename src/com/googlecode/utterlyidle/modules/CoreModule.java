package com.googlecode.utterlyidle.modules;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.SystemClock;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.cookies.CookieParameters;
import com.googlecode.utterlyidle.handlers.*;
import com.googlecode.utterlyidle.rendering.ExceptionRenderer;
import com.googlecode.utterlyidle.rendering.MatchFailureRenderer;
import com.googlecode.utterlyidle.rendering.ObjectRenderer;
import com.googlecode.utterlyidle.rendering.SeeOtherRenderer;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;
import com.googlecode.yadic.generics.TypeFor;
import com.googlecode.yadic.resolvers.OptionResolver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.UUID;

import static com.googlecode.totallylazy.Predicates.*;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.URLs.packageUrl;
import static com.googlecode.totallylazy.Unchecked.cast;
import static com.googlecode.utterlyidle.Status.*;
import static com.googlecode.utterlyidle.dsl.DslBindings.bindings;
import static com.googlecode.utterlyidle.dsl.StaticBindingBuilder.in;
import static com.googlecode.utterlyidle.handlers.HandlerRule.entity;
import static com.googlecode.utterlyidle.handlers.HandlerRule.status;
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
        container.add(Redirector.class, BaseUriRedirector.class);
        container.add(RequestGenerator.class, BindingsRequestGenerator.class);
        container.add(ResponseHandlersFinder.class);
        container.add(Auditors.class, Auditors.class);
        container.addActivator(Auditor.class, container.getActivator(Auditors.class));
        container.add(HttpClient.class, ClientHttpHandler.class);
        container.add(InternalHttpHandler.class);
        container.decorate(HttpClient.class, SmartHttpClient.class);
        return this;
    }

    @Override
    public Module addPerApplicationObjects(Container container) {
        container.add(Clock.class, SystemClock.class);
        container.add(Resources.class, RegisteredResources.class);
        container.addActivator(Bindings.class, container.getActivator(Resources.class));
        container.add(ResponseHandlers.class);
        container.add(ApplicationId.class);
        container.add(InternalRequestMarker.class);
        return this;
    }

    @Override
    public Module addResources(Resources resources) {
        resources.add(bindings(in(packageUrl(Application.class)).path("utterlyidle")));
        return this;
    }

    @Override
    public Module addResponseHandlers(ResponseHandlers handlers) {
        handlers.addGuard(where(entity(), nullOrEmptyString()), NoContentHandler.class);
        handlers.addGuard(where(entity(), is(instanceOf(byte[].class)).or(instanceOf(StreamingWriter.class)).or(instanceOf(StreamingOutput.class))), IdentityHandler.class);
        handlers.addCatchAll(where(status(), is(SEE_OTHER)).and(where(entity(), is(instanceOf(String.class)))), renderer(SeeOtherRenderer.class));
        handlers.addCatchAll(where(entity(), is(instanceOf(MatchFailure.class))), renderer(MatchFailureRenderer.class));
        handlers.addCatchAll(where(entity(), is(instanceOf(Exception.class))), renderer(ExceptionRenderer.class));
        handlers.addCatchAll(where(entity(), is(instanceOf(Object.class))), renderer(ObjectRenderer.class));
        return this;
    }

    @Override
    public Module addPerArgumentObjects(Container argumentScope) throws Exception {
        Request request = argumentScope.get(Request.class);
        argumentScope.addActivator(PathParameters.class, PathParametersActivator.class);
        argumentScope.addInstance(HeaderParameters.class, request.headers());
        argumentScope.addInstance(QueryParameters.class, Requests.query(request));
        argumentScope.addInstance(FormParameters.class, Requests.form(request));
        argumentScope.addInstance(CookieParameters.class, Requests.cookies(request));
        argumentScope.addInstance(Entity.class, request.entity());
        argumentScope.addInstance(InputStream.class, new ByteArrayInputStream(request.entity().asBytes()));
        argumentScope.addType(new TypeFor<Option<?>>() {}.get(), new OptionResolver(argumentScope, instanceOf(IllegalArgumentException.class)));
        argumentScope.addType(new TypeFor<Either<?, ?>>() {}.get(), EitherResolver.class);
        argumentScope.addActivator(UUID.class, UUIDActivator.class);
        argumentScope.addType(new TypeFor<Iterable<UUID>>() {}.get(), new TypedIterableResolver<UUID>(argumentScope, UUIDActivator.fromString()));
        return this;
    }

    private Predicate<Object> nullOrEmptyString() {
        return new Predicate<Object>() {
            @Override
            public boolean matches(Object other) {
                return other == null || ((other instanceof String) && ((String)other).isEmpty());
            }
        };
    }

    public static class TypedIterableResolver<T> implements Resolver<Iterable<T>> {
        private final Container container;
        private final Callable1<String, T> mapper;

        public TypedIterableResolver(Container container, Callable1<String, T> mapper) {
            this.container = container;
            this.mapper = mapper;
        }

        @Override
        public Iterable<T> resolve(Type type) throws Exception {
            Iterable<String> values = cast(container.resolve(new TypeFor<Iterable<String>>() {}.get()));
            return sequence(values).map(mapper).realise();
        }
    }
}