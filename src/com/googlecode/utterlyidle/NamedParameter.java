package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;
import com.googlecode.yadic.TypeMap;
import com.googlecode.totallylazy.reflection.TypeFor;

import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Unchecked.cast;
import static com.googlecode.totallylazy.functions.Callables.callThrows;

public class NamedParameter implements Parameter {
    private final String name;
    private final Option<String> defaultValue;
    private final Class<? extends Parameters<?>> parametersClass;

    public NamedParameter(String name, Class<? extends Parameters<?>> parametersClass, Option<String> defaultValue) {
        this.name = name;
        this.parametersClass = parametersClass;
        this.defaultValue = defaultValue;
    }

    public String name() {
        return name;
    }

    public Option<String> defaultValue() {
        return defaultValue;
    }

    public Class<? extends Parameters<?>> parametersClass() {
        return parametersClass;
    }

    public Callable<String> extractValueFrom(final TypeMap typeMap) {
        return () -> {
            Parameters<?> parameters = cast(typeMap.resolve(parametersClass()));
            if (!parameters.contains(name())) {
                return defaultValueOrThrow();
            }
            return parameters.getValue(name());
        };
    }

    private String defaultValueOrThrow() {
        return defaultValue.getOrElse(callThrows(new IllegalArgumentException(), String.class));
    }

    public Resolver<Iterable<String>> extractValuesFrom(final TypeMap typeMap) {
        return type -> {
            Parameters<?> parameters = cast(typeMap.resolve(parametersClass()));
            if (!parameters.contains(name())) {
                return sequence(defaultValueOrThrow());
            }
            return parameters.getValues(name());
        };
    }

    public void addTo(Container container) {
        container.addActivator(String.class, extractValueFrom(container)).
                addType(new TypeFor<Iterable<String>>() {}.get(), extractValuesFrom(container));
    }

    public static class methods {
        public static Function1<NamedParameter, Option<String>> defaultValue() {
            return NamedParameter::defaultValue;
        }
    }
}
