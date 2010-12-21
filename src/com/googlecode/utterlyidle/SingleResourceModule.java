package com.googlecode.utterlyidle;

public class SingleResourceModule implements RestModule {
    private final Class<?> resource;

    public SingleResourceModule(Class<?> resource) {
        this.resource = resource;
    }

    public Module addResources(Engine engine) {
        engine.add(resource);
        return this;
    }
}
