package com.googlecode.utterlyidle.modules;

import com.googlecode.yadic.Container;

public class RequestInstanceModule implements RequestScopedModule {
    private final Object instance;

    public RequestInstanceModule(Object instance) {
        this.instance = instance;
    }

    public Module addPerRequestObjects(Container container) {
        Class aClass = instance.getClass();
        container.addInstance(aClass, instance);
        return this;
    }
}
