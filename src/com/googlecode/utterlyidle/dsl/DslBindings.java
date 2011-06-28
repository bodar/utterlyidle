package com.googlecode.utterlyidle.dsl;

import com.googlecode.utterlyidle.Binding;

public class DslBindings {
    public static Binding binding(BindingBuilder bindingBuilder) {
        return bindingBuilder.build();
    }
}
