package com.googlecode.utterlyidle.modules;

import com.googlecode.yadic.Container;

public class RequestScopedClass implements RequestScopedModule {
    private final Class<?> aClass;

    public RequestScopedClass(Class<?> aClass) {
        this.aClass = aClass;
    }

    public Module addPerRequestObjects(Container container) {

        container.add(aClass);
        return this;
    }
}
