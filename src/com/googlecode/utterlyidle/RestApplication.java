package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Runnable1;
import com.googlecode.utterlyidle.handlers.CloseHandler;
import com.googlecode.utterlyidle.handlers.ExceptionHandler;
import com.googlecode.utterlyidle.handlers.Renderers;
import com.googlecode.utterlyidle.handlers.ResponseHandlerRegistry;
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
        requestScope.decorate(HttpHandler.class, ExceptionHandler.class);
        requestScope.decorate(HttpHandler.class, CloseHandler.class);
        return requestScope;
    }

    public Application add(Module module) {
        sequence(module).safeCast(ApplicationScopedModule.class).forEach(addPerApplicationObjects(applicationScope));
        sequence(module).safeCast(ResourcesModule.class).forEach(addResources(resources()));
        sequence(module).safeCast(ResponseHandlersModule.class).forEach(addResponseHandlers(responseHandlers()));
        sequence(module).safeCast(RenderersModule.class).forEach(addRenderers(renderers()));
        modules.add(module);
        return this;
    }

    public Container applicationScope() {
        return applicationScope;
    }

    public void handle(Request request, Response response) throws Exception {
        createRequestScope(request, response).get(HttpHandler.class).handle(request, response);
    }

    private Container createRequestScope(Request request, Response response) {
        Container requestScope = createRequestScope();
        requestScope.addInstance(Request.class, request);
        requestScope.addInstance(Response.class, response);
        return requestScope;
    }

    public Resources resources() {
        return applicationScope.get(Resources.class);
    }

    public ResponseHandlerRegistry responseHandlers() {
        return applicationScope.get(ResponseHandlerRegistry.class);
    }

    public Renderers renderers() {
        return applicationScope.get(Renderers.class);
    }

    private Runnable1<RenderersModule> addRenderers(final Renderers renderers) {
        return new Runnable1<RenderersModule>() {
            public void run(RenderersModule renderersModule) {
                renderersModule.addRenderers(renderers);
            }
        };
    }

    private Runnable1<ResponseHandlersModule> addResponseHandlers(final ResponseHandlerRegistry registry) {
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