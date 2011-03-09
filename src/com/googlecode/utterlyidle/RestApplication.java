package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.utterlyidle.handlers.ExceptionHandler;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.modules.ApplicationScopedModule;
import com.googlecode.utterlyidle.modules.CoreModule;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.utterlyidle.modules.ResponseHandlersModule;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;
import com.googlecode.yadic.SimpleContainer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.modules.Modules.addPerApplicationObjects;
import static com.googlecode.utterlyidle.modules.Modules.addPerRequestObjects;
import static com.googlecode.utterlyidle.modules.Modules.addResources;
import static com.googlecode.utterlyidle.modules.Modules.addResponseHandlers;

public class RestApplication implements Application {
    private final Container applicationScope = new SimpleContainer();
    private final List<Module> modules = new ArrayList<Module>();

    public RestApplication() {
        applicationScope.addInstance(Application.class, this);
        add(new CoreModule());
    }

    public Container createRequestScope() {
        final Container requestScope = new SimpleContainer(applicationScope);
        requestScope.addInstance(Container.class, requestScope);
        requestScope.addActivator(Resolver.class, requestScope.getActivator(Container.class));
        requestScope.add(HttpHandler.class, BaseHandler.class);
        sequence(modules).safeCast(RequestScopedModule.class).forEach(addPerRequestObjects(requestScope));
        requestScope.decorate(HttpHandler.class, AbsoluteLocationHandler.class);
        requestScope.decorate(HttpHandler.class, ExceptionHandler.class);
        return requestScope;
    }

    public Application add(Module module) {
        sequence(module).safeCast(ApplicationScopedModule.class).forEach(addPerApplicationObjects(applicationScope));
        sequence(module).safeCast(ResourcesModule.class).forEach(addResources(applicationScope.get(Resources.class)));
        sequence(module).safeCast(ResponseHandlersModule.class).forEach(addResponseHandlers(applicationScope.get(ResponseHandlers.class)));
        modules.add(module);
        return this;
    }

    public Container applicationScope() {
        return applicationScope;
    }

    public Response handle(final Request request) throws Exception {
        return using(createRequestScope(), handleRequest(request));
    }

    public static Callable1<Container, Response> handleRequest(final Request request) {
        return new Callable1<Container, Response>(){
            public Response call(Container container) throws Exception {
                return container.get(HttpHandler.class).handle(request);
            }
        };
    }

    public void close() throws IOException {
        applicationScope().close();
    }
}