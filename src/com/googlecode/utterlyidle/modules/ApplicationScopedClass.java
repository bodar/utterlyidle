package com.googlecode.utterlyidle.modules;

import com.googlecode.yadic.Container;

public class ApplicationScopedClass implements ApplicationScopedModule {
    private final Class<?> aClass;

    public ApplicationScopedClass(Class<?> aClass) {
        this.aClass = aClass;
    }

    public Module addPerApplicationObjects(Container container) {
        container.add(aClass);
        return this;
    }
}
