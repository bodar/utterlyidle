package com.googlecode.utterlyidle.modules;

public interface ModuleDefiner extends Module {
    Module defineModules(ModuleDefinitions moduleDefinitions) throws Exception;
}
