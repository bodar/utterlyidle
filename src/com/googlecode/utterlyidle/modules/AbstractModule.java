package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.handlers.Auditors;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.yadic.Container;

public abstract class AbstractModule implements
        ApplicationScopedModule, RequestScopedModule, ResourcesModule, ResponseHandlersModule, ModuleDefiner, AuditModule, ArgumentScopedModule {

    public Module defineModules(ModuleDefinitions moduleDefinitions) throws Exception {
        return this;
    }

    public Module addPerRequestObjects(Container container) throws Exception {
        return this;
    }

    public Module addPerApplicationObjects(Container container) throws Exception {
        return this;
    }

    public Module addResources(Resources resources) throws Exception {
        return this;
    }

    public Module addResponseHandlers(ResponseHandlers handlers) throws Exception {
        return this;
    }

    public Auditors addAuditors(Auditors auditors) throws Exception {
        return auditors;
    }

    @Override
    public Module addPerArgumentObjects(Container container) throws Exception {
        return this;
    }
}
