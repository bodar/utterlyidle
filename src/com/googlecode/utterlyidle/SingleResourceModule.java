package com.googlecode.utterlyidle;

public class SingleResourceModule implements ResourcesModule {
    private final Class<?> resource;

    public SingleResourceModule(Class<?> resource) {
        this.resource = resource;
    }

    public Module addResources(Resources resources) {
        resources.add(resource);
        return this;
    }
}
