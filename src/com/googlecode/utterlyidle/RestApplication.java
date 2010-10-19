package com.googlecode.utterlyidle;

import com.googlecode.yadic.Container;
import com.googlecode.yadic.SimpleContainer;

import java.util.ArrayList;
import java.util.List;

public class RestApplication implements Application {
    private final Container applicationScope = new SimpleContainer();
    private final List<Module> modules = new ArrayList<Module>();

    public RestApplication() {
        add(new CoreModule());
    }

    public Container createRequestScope() {
        Container requestScope = new SimpleContainer(applicationScope);
        for (Module module : modules) {
            module.addPerRequestObjects(requestScope);
        }
        return requestScope;
    }

    public Application add(Module module) {
        module.addPerApplicationObjects(applicationScope);
        module.addResources(applicationScope.get(RestEngine.class));
        modules.add(module);
        return this;
    }

    public Container applicationScope() {
        return applicationScope;
    }

    public void handle(Request request, Response response) {
        applicationScope.get(RestEngine.class).handle(createRequestScope(), request, response);
    }
}