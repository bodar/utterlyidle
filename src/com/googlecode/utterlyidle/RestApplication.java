package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Runnable1;
import com.googlecode.utterlyidle.handlers.CookiesHandler;
import com.googlecode.utterlyidle.handlers.ExceptionHandler;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.modules.*;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;
import com.googlecode.yadic.SimpleContainer;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.Sequences.sequence;

public class RestApplication implements Application {
    private final Container applicationScope = new SimpleContainer();
    private final List<Module> modules = new ArrayList<Module>();

    public RestApplication() {
        applicationScope.addInstance(Application.class, this);
        add(new CoreModule());
    }

    public Container createRequestScope() {
        final Container requestScope = new SimpleContainer(applicationScope);
        requestScope.addInstance(Resolver.class, requestScope);
        requestScope.add(HttpHandler.class, BaseHandler.class);
        sequence(modules).safeCast(RequestScopedModule.class).forEach(addPerRequestObjects(requestScope));
        requestScope.decorate(HttpHandler.class, CookiesHandler.class);
        requestScope.decorate(HttpHandler.class, ExceptionHandler.class);
        return requestScope;
    }

    public Application add(Module module) {
        sequence(module).safeCast(ApplicationScopedModule.class).forEach(addPerApplicationObjects(applicationScope));
        sequence(module).safeCast(ResourcesModule.class).forEach(addResources(resources()));
        sequence(module).safeCast(ResponseHandlersModule.class).forEach(addResponseHandlers(responseHandlers()));
        modules.add(module);
        return this;
    }

    public Container applicationScope() {
        return applicationScope;
    }

    public Response handle(Request request) throws Exception {
        return createRequestScope(request).get(HttpHandler.class).handle(request);
    }

    private Container createRequestScope(Request request) {
        Container requestScope = createRequestScope();
        requestScope.addInstance(Request.class, request);
        return requestScope;
    }

    public Resources resources() {
        return applicationScope.get(Resources.class);
    }

    public ResponseHandlers responseHandlers() {
        return applicationScope.get(ResponseHandlers.class);
    }

    private Runnable1<ResponseHandlersModule> addResponseHandlers(final ResponseHandlers registry) {
        return new Runnable1<ResponseHandlersModule>() {
            public void run(ResponseHandlersModule responseHandlersModule) {
                responseHandlersModule.addResponseHandlers(registry);
            }
        };
    }

    private Runnable1<RequestScopedModule> addPerRequestObjects(final Container requestScope) {
        return new Runnable1<RequestScopedModule>() {
            public void run(RequestScopedModule requestScopedModule) {
                requestScopedModule.addPerRequestObjects(requestScope);
            }
        };
    }

    private Runnable1<ResourcesModule> addResources(final Resources resources) {
        return new Runnable1<ResourcesModule>() {
            public void run(ResourcesModule resourcesModule) {
                resourcesModule.addResources(resources);
            }
        };
    }

    private Runnable1<ApplicationScopedModule> addPerApplicationObjects(final Container applicationScope) {
        return new Runnable1<ApplicationScopedModule>() {
            public void run(ApplicationScopedModule applicationScopedModule) {
                applicationScopedModule.addPerApplicationObjects(applicationScope);
            }
        };
    }


}