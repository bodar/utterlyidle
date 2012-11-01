package com.googlecode.utterlyidle.modules;

public interface ModuleDefinitions {
    ModuleDefinitions addApplicationModule(Class<? extends Module> moduleClass);

    ModuleDefinitions addRequestModule(Class<? extends Module> moduleClass);

    ModuleDefinitions addArgumentModule(Class<? extends Module> moduleClass);
}
