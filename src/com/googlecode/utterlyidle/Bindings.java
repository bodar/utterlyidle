package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Option;

import java.lang.reflect.Method;

public interface Bindings extends Iterable<Binding> {
    public Option<Binding> find(Method method);
}
