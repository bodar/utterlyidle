package com.googlecode.utterlyidle.modules;

import com.googlecode.yadic.Container;

public interface ModuleActivator {
    ModuleActivator activateApplicationModule(Module module, Container applicationScope);

    ModuleActivator activateRequestModules(Container requestScope);

    ModuleActivator activateArgumentModules(Container argumentScope);
}
