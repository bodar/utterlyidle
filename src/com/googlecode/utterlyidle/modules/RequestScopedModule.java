package com.googlecode.utterlyidle.modules;

import com.googlecode.yadic.Container;

public interface RequestScopedModule extends Module {
    Container addPerRequestObjects(Container container) throws Exception;

}
