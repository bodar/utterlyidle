package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Option;
import com.googlecode.utterlyidle.cookies.CookieParameters;
import com.googlecode.utterlyidle.handlers.AuditHandler;
import com.googlecode.utterlyidle.handlers.ExceptionHandler;
import com.googlecode.utterlyidle.io.HierarchicalPath;
import com.googlecode.utterlyidle.io.Url;
import com.googlecode.utterlyidle.modules.CoreModule;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.ModuleDefinitions;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;
import com.googlecode.yadic.SimpleContainer;
import com.googlecode.yadic.generics.TypeFor;
import com.googlecode.yadic.resolvers.OptionResolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Predicates.instanceOf;

public class RestApplication implements Application {
    private final Container applicationScope = new SimpleContainer();
    private final List<Module> modules = new ArrayList<Module>();
    private final ModuleDefinitions definitions = new ModuleDefinitions();

    public RestApplication(Module... modules) {
        applicationScope.addInstance(Application.class, this);
        applicationScope.addInstance(ModuleDefinitions.class, definitions);
        applicationScope.addInstance(Container.class, applicationScope);
        add(new CoreModule());
        for (Module module : modules) {
            add(module);
        }
    }

    public Application add(Module module) {
        definitions.activateApplicationModule(module, applicationScope);
        modules.add(module);
        return this;
    }

    public Container applicationScope() {
        return applicationScope;
    }

    public Response handle(final Request request) throws Exception {
        return usingRequestScope(handleRequest(request));
    }

    public <T> T usingRequestScope(Callable1<Container, T> callable) {
        return using(createRequestScope(), callable);
    }

    private Container createRequestScope() {
        final Container requestScope = new SimpleContainer(applicationScope);
        requestScope.addInstance(Container.class, requestScope);
        requestScope.addActivator(Resolver.class, requestScope.getActivator(Container.class));
        requestScope.add(HttpHandler.class, BaseHandler.class);
        definitions.activateRequestModules(modules, requestScope);
        addServerUrlIfNeeded(requestScope);
        requestScope.decorate(HttpHandler.class, AbsoluteLocationHandler.class);
        requestScope.decorate(HttpHandler.class, ExceptionHandler.class);
        requestScope.decorate(HttpHandler.class, AuditHandler.class);
        return requestScope;
    }

    private void addServerUrlIfNeeded(Container requestScope) {
        if (!requestScope.contains(ServerUrl.class)) {
            requestScope.addInstance(ServerUrl.class, ServerUrl.serverUrl("/")); // Ideally all RestServers should add a ServerUrl but for internal requests this is not required
        }
    }

    public <T> T usingParameterScope(Request request, Callable1<Container, T> callable) {
        return using(createArgumentScope(request), callable);
    }

    private Container createArgumentScope(Request request) {
        final Container argumentScope = new SimpleContainer();
        argumentScope.addInstance(Request.class, request);
        argumentScope.addInstance(Url.class, request.url());
        argumentScope.addInstance(HierarchicalPath.class, request.url().path());
        argumentScope.addActivator(PathParameters.class, PathParametersActivator.class);
        argumentScope.addInstance(HeaderParameters.class, request.headers());
        argumentScope.addInstance(QueryParameters.class, request.query());
        argumentScope.addInstance(FormParameters.class, request.form());
        argumentScope.addInstance(CookieParameters.class, request.cookies());
        argumentScope.addInstance(InputStream.class, new ByteArrayInputStream(request.input()));
        argumentScope.add(new TypeFor<Option<?>>() {
        }.get(), new OptionResolver(argumentScope, instanceOf(IllegalArgumentException.class)));
        argumentScope.add(new TypeFor<Either<?, ?>>() {
        }.get(), new EitherResolver(argumentScope));
        argumentScope.addInstance(Container.class, argumentScope);
        definitions.activateArgumentModules(modules, argumentScope);
        return argumentScope;
    }

    public static Callable1<Container, Response> handleRequest(final Request request) {
        return new Callable1<Container, Response>() {
            public Response call(Container container) throws Exception {
                return container.get(HttpHandler.class).handle(request);
            }
        };
    }

    public static <T, R> Callable1<Container, R> inject(final T instance, final Callable1<Container, R> handler) {
        return new Callable1<Container, R>() {
            public R call(Container container) throws Exception {
                if (container.contains(instance.getClass())) {
                    container.remove(instance.getClass());
                }
                return handler.call(container.addInstance((Class) instance.getClass(), instance));
            }
        };
    }

    public void close() throws IOException {
        applicationScope().close();
    }
}