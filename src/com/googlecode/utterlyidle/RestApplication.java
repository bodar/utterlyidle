package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Runnable1;
import com.googlecode.utterlyidle.handlers.CloseHandler;
import com.googlecode.utterlyidle.handlers.ExceptionHandler;
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
        sequence(module).safeCast(RestModule.class).forEach(addResources(engine()));
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

    public Engine engine() {
        return applicationScope.get(Engine.class);
    }

    private Runnable1<RequestScopedModule> addPerRequestObjects(final Container requestScope) {
        return new Runnable1<RequestScopedModule>() {
            public void run(RequestScopedModule requestScopedModule) {
                requestScopedModule.addPerRequestObjects(requestScope);
            }
        };
    }

    private Runnable1<RestModule> addResources(final Engine engine) {
        return new Runnable1<RestModule>() {
            public void run(RestModule restModule) {
                restModule.addResources(engine);
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