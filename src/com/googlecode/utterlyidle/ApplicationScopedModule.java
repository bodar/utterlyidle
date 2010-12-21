package com.googlecode.utterlyidle;

import com.googlecode.yadic.Container;

public interface ApplicationScopedModule extends Module {
    Module addPerApplicationObjects(Container container);
}
