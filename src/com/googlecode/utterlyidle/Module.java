package com.googlecode.utterlyidle;

import com.googlecode.yadic.Container;

public interface Module {
    Module addPerRequestObjects(Container container);

    Module addPerApplicationObjects(Container container);

    Module addResources(Engine engine);
}
