package com.googlecode.utterlyidle;

import com.googlecode.yadic.Container;

class SingleResourceModule implements Module {
    private final Class<?> resource;

    public SingleResourceModule(Class<?> resource) {
        this.resource = resource;
    }

    public Module addPerRequestObjects(Container container) {
        return this;
    }

    public Module addPerApplicationObjects(Container container) {
        return this;
    }

    public Module addResources(Engine engine) {
        engine.add(resource);
        return this;
    }
}
