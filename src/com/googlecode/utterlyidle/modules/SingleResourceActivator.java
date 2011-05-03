package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.dsl.BindingBuilder;

public class SingleResourceActivator implements ResourcesModule {
    private final BindingBuilder bindingBuilder;

    public SingleResourceActivator(final BindingBuilder bindingBuilder) {
        this.bindingBuilder = bindingBuilder;
    }

    public Module addResources(Resources resources) {
        resources.add(bindingBuilder);
        return this;
    }
}
