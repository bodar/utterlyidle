package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.Resources;

import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;

public class SingleResourceModule implements ResourcesModule {
    private final Class<?> resource;

    public SingleResourceModule(Class<?> resource) {
        this.resource = resource;
    }

    public Module addResources(Resources resources) {
        resources.add(annotatedClass(resource));
        return this;
    }
}
