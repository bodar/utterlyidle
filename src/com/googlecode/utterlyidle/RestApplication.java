package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.handlers.ExceptionHandler;
import com.googlecode.utterlyidle.handlers.FlushResponseHandler;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;
import com.googlecode.yadic.SimpleContainer;

import java.util.ArrayList;
import java.util.List;

public class RestApplication implements Application {
    private final Container applicationScope = new SimpleContainer();
    private final List<Module> modules = new ArrayList<Module>();

    public RestApplication() {
        add(new CoreModule());
    }

    private Container createRequestScope(Request request, Response response) {
        final Container requestScope = new SimpleContainer(applicationScope);
        requestScope.addInstance(Resolver.class, requestScope);
        requestScope.addInstance(Request.class, request);
        requestScope.addInstance(Response.class, response);
        requestScope.add(RequestHandler.class, RestRequestHandler.class);
        for (Module module : modules) {
            module.addPerRequestObjects(requestScope);
        }
        requestScope.decorate(RequestHandler.class, ExceptionHandler.class);
        requestScope.decorate(RequestHandler.class, FlushResponseHandler.class);
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
        createRequestScope(request, response).get(RequestHandler.class).handle(request, response);
    }

    public Engine engine() {
        return applicationScope.get(Engine.class);
    }

}