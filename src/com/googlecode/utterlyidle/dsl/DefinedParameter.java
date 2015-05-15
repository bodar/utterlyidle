package com.googlecode.utterlyidle.dsl;

import com.googlecode.totallylazy.Value;
import com.googlecode.utterlyidle.Parameter;
import com.googlecode.yadic.Container;

import static com.googlecode.totallylazy.Callables.returns;
import static com.googlecode.yadic.resolvers.Resolvers.asResolver;

public class DefinedParameter<T> implements Value<T>, Parameter {
    private final java.lang.reflect.Type type;
    private final T value;

    public DefinedParameter(java.lang.reflect.Type type, T value) {
        this.type = type;
        this.value = value;
    }

    public T value() {
        return value;
    }

    public void addTo(Container container) {
        container.addType(type, asResolver(returns(value)));
    }
}
