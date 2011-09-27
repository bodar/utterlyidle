package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.cookies.CookieParameters;
import com.googlecode.utterlyidle.handlers.AuditHandler;
import com.googlecode.utterlyidle.handlers.ExceptionHandler;
import com.googlecode.utterlyidle.io.HierarchicalPath;
import com.googlecode.utterlyidle.modules.CoreModule;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.Modules;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.SimpleContainer;
import com.googlecode.yadic.generics.TypeFor;
import com.googlecode.yadic.resolvers.OptionResolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.utterlyidle.io.HierarchicalPath.hierarchicalPath;

public class RestApplication implements Application {
    private final Container applicationScope = new SimpleContainer();
    private final Modules modules = new Modules();

    public RestApplication(Module... modules) {
        applicationScope.addInstance(Application.class, this);
        this.modules.setupApplicationScope(applicationScope);
        add(new CoreModule());
        for (Module module : modules) {
            add(module);
        }
    }

    public Application add(Module module) {
        modules.activateApplicationModule(module, applicationScope);
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
        requestScope.add(HttpHandler.class, BaseHandler.class);
        modules.activateRequestModules(requestScope);
        addBasePathIfNeeded(requestScope);
        requestScope.decorate(HttpHandler.class, BasePathHandler.class);
        requestScope.decorate(HttpHandler.class, ExceptionHandler.class);
        requestScope.decorate(HttpHandler.class, AuditHandler.class);
        return requestScope;
    }

    private void addBasePathIfNeeded(Container requestScope) {
        if (!requestScope.contains(BasePath.class)) {
            requestScope.addInstance(BasePath.class, BasePath.basePath("/"));
        }
    }

    public <T> T usingParameterScope(Request request, Callable1<Container, T> callable) {
        return using(createArgumentScope(request), callable);
    }

    private Container createArgumentScope(Request request) {
        final Container argumentScope = new SimpleContainer();
        argumentScope.addInstance(Request.class, request);
        argumentScope.addInstance(Uri.class, request.uri());
        argumentScope.addInstance(HierarchicalPath.class, hierarchicalPath(request.uri().path()));
        argumentScope.addActivator(PathParameters.class, PathParametersActivator.class);
        argumentScope.addInstance(HeaderParameters.class, request.headers());
        argumentScope.addInstance(QueryParameters.class, Requests.query(request));
        argumentScope.addInstance(FormParameters.class, Requests.form(request));
        argumentScope.addInstance(CookieParameters.class, Requests.cookies(request));
        argumentScope.addInstance(InputStream.class, new ByteArrayInputStream(request.input()));
        argumentScope.add(new TypeFor<Option<?>>() {
        }.get(), new OptionResolver(argumentScope, instanceOf(IllegalArgumentException.class)));
        argumentScope.add(new TypeFor<Either<?, ?>>() {
        }.get(), new EitherResolver(argumentScope));
        modules.activateArgumentModules(argumentScope);
        return argumentScope;
    }

    public static Callable1<Container, Response> handleRequest(final Request request) {
        return new Callable1<Container, Response>() {
            public Response call(Container container) throws Exception {
                return container.addInstance(Request.class, request).
                        get(HttpHandler.class).handle(request);
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