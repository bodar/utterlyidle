package com.googlecode.utterlyidle.modules;

import com.googlecode.yadic.Container;

public interface ArgumentScopedModule extends Module {
    Module addPerArgumentObjects(Container container);

}
