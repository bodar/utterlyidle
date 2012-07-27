package com.googlecode.utterlyidle.modules;

import com.googlecode.yadic.Container;

public interface RequestScopedModule extends Module {
    Module addPerRequestObjects(Container container) throws Exception;

}
