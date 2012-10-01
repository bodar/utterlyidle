package com.googlecode.utterlyidle.dsl;

import com.googlecode.utterlyidle.Binding;

import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Callers.call;

public class DslBindings {
    public static Binding binding(Callable<Binding> builder) {
        return call(builder);
    }

    public static Binding[] bindings(Callable<Binding[]> builder) {
        return call(builder);
    }
}
