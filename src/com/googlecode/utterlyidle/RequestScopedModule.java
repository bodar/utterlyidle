package com.googlecode.utterlyidle;

import com.googlecode.yadic.Container;

public interface RequestScopedModule extends Module {
    Module addPerRequestObjects(Container container);

}
