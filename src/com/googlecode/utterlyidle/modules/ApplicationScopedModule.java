package com.googlecode.utterlyidle.modules;

import com.googlecode.yadic.Container;

public interface ApplicationScopedModule extends Module {
    Module addPerApplicationObjects(Container container) throws Exception;
}
