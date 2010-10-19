package com.googlecode.utterlyidle;

import com.googlecode.yadic.Container;


public class CoreModule implements Module{
    public Module addPerRequestObjects(Container container) {
        container.add(BasePath.class);
        return this;
    }

    public Module addPerApplicationObjects(Container container) {
        container.add(RestEngine.class);
        return this;
    }

    public Module addResources(Engine engine) {
        return this;
    }
}