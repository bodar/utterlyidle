package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.dsl.BindingBuilder;
import com.googlecode.utterlyidle.dsl.DslBindings;

import static com.googlecode.utterlyidle.dsl.DslBindings.binding;

public class SingleResourceActivator implements ResourcesModule {
    private final BindingBuilder bindingBuilder;

    public SingleResourceActivator(final BindingBuilder bindingBuilder) {
        this.bindingBuilder = bindingBuilder;
    }

    public Module addResources(Resources resources) {
        resources.add(binding(bindingBuilder));
        return this;
    }
}
