package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.Engine;
import com.googlecode.utterlyidle.Module;
import com.googlecode.utterlyidle.rendering.HelloWorld;
import com.googlecode.yadic.Container;

public class RestModule implements Module {
    public Module addPerRequestObjects(Container container) {
        container.add(HelloWorld.class);
        return this;
    }

    public Module addPerApplicationObjects(Container container) {
        return this;
    }

    public Module addResources(Engine engine) {
        engine.add(HelloWorld.class);
        return this;
    }
}
