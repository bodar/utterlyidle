package com.googlecode.utterlyidle.modules;

public interface ModuleDefiner extends Module {
    ModuleDefinitions defineModules(ModuleDefinitions moduleDefinitions) throws Exception;
}
