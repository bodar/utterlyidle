package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.modules.ModuleDefiner;
import com.googlecode.utterlyidle.modules.ModuleDefinitions;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.yadic.Container;

public abstract class ActivateSiteMeshModule implements RequestScopedModule, ModuleDefiner, SiteMeshModule {
    public Container addPerRequestObjects(Container container) {
        return container.add(Decorators.class).
                addInstance(DecoratorProvider.class, provider(container)).
                decorate(HttpHandler.class, SiteMeshHandler.class);
    }

    public ModuleDefinitions defineModules(ModuleDefinitions moduleDefinitions) {
        return moduleDefinitions.addRequestModule(SiteMeshModule.class);
    }

    protected abstract DecoratorProvider provider(Container container);
}
