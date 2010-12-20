package com.googlecode.utterlyidle;

import com.googlecode.yadic.Container;

public class AbstractModule implements Module {
    public Module addPerRequestObjects(Container container) {
        return this;
    }

    public Module addPerApplicationObjects(Container container) {
        return this;
    }

    public Module addResources(Engine engine) {
        return this;
    }
}
