package com.googlecode.utterlyidle;

import com.googlecode.yadic.Container;

class TestModule implements Module {
    private final Class<?> resource;

    public TestModule(Class<?> resource) {
        this.resource = resource;
    }

    public Module addPerRequestObjects(Container container) {
        container.add(resource);
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
