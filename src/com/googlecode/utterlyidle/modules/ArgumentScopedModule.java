package com.googlecode.utterlyidle.modules;

import com.googlecode.yadic.Container;

public interface ArgumentScopedModule extends Module {
    Container addPerArgumentObjects(Container container) throws Exception;

}
