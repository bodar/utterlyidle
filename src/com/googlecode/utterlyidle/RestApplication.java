package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.handlers.CloseHandler;
import com.googlecode.utterlyidle.handlers.ExceptionHandler;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;
import com.googlecode.yadic.SimpleContainer;

import java.util.ArrayList;
import java.util.List;

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
        for (Module module : modules) {
            module.addPerRequestObjects(requestScope);
        }
        requestScope.decorate(HttpHandler.class, ExceptionHandler.class);
        requestScope.decorate(HttpHandler.class, CloseHandler.class);
        return requestScope;
    }

    public Application add(Module module) {
        module.addPerApplicationObjects(applicationScope);
        module.addResources(engine());
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

}