package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.Binding;
import com.googlecode.utterlyidle.Resources;

public class BindingsModule implements ResourcesModule {
    private final Binding[] bindings;

    public BindingsModule(Binding[] bindings) {
        this.bindings = bindings;
    }

    public Module addResources(Resources resources) {
        resources.add(bindings);
        return this;
    }
}
