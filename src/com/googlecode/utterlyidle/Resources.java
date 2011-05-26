package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.dsl.BindingBuilder;

public interface Resources extends Bindings {
    void add(Class annotatedClass);

    void add(Binding binding);

    void add(BindingBuilder binding);
}
