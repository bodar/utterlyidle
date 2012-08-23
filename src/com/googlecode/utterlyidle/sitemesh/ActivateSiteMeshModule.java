package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.ModuleDefiner;
import com.googlecode.utterlyidle.modules.ModuleDefinitions;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.yadic.Container;

public abstract class ActivateSiteMeshModule implements RequestScopedModule, ModuleDefiner, SiteMeshModule {
    public Module addPerRequestObjects(Container container) {
        container.add(Decorators.class);
        container.addInstance(DecoratorProvider.class, provider(container));
        container.decorate(HttpHandler.class, SiteMeshHandler.class);
        return this;
    }

    public Module defineModules(ModuleDefinitions moduleDefinitions) {
        moduleDefinitions.addRequestModule(SiteMeshModule.class);
        return this;
    }

    protected abstract DecoratorProvider provider(Container container);
}
