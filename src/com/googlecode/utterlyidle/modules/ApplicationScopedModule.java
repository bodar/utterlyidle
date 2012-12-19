package com.googlecode.utterlyidle.modules;

import com.googlecode.yadic.Container;

public interface ApplicationScopedModule extends Module {
    Container addPerApplicationObjects(Container container) throws Exception;
}
