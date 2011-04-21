package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.dsl.ActivatorBuilder;

public class SingleResourceActivator implements ResourcesModule {
    private final ActivatorBuilder activatorBuilder;

    public SingleResourceActivator(final ActivatorBuilder activatorBuilder) {
        this.activatorBuilder = activatorBuilder;
    }

    public Module addResources(Resources resources) {
        resources.add(activatorBuilder);
        return this;
    }
}
