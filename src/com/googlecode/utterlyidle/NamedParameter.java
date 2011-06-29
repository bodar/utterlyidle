package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Option;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;
import com.googlecode.yadic.TypeMap;
import com.googlecode.yadic.generics.TypeFor;

import java.lang.reflect.Type;

import static com.googlecode.totallylazy.Callables.callThrows;
import static com.googlecode.totallylazy.Sequences.sequence;

public class NamedParameter implements Parameter {
    private final String name;
    private final Option<String> defaultValue;
    private final Class<? extends Parameters<String, String>> parametersClass;

    public NamedParameter(String name, Class<? extends Parameters<String, String>> parametersClass, Option<String> defaultValue) {
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

    public Class<? extends Parameters<String, String>> parametersClass() {
        return parametersClass;
    }

    public Resolver<String> extractValueFrom(final TypeMap typeMap) {
        return new Resolver<String>() {
            public String resolve(Type type) throws Exception {
                Parameters<String, String> parameters = (Parameters<String, String>) typeMap.resolve(parametersClass());
                if (!parameters.contains(name())) {
                    return defaultValueOrThrow();
                }
                return parameters.getValue(name());
            }
        };
    }

    private String defaultValueOrThrow() {
        return defaultValue.getOrElse(callThrows(new IllegalArgumentException(), String.class));
    }

    public Resolver<Iterable<String>> extractValuesFrom(final TypeMap typeMap) {
        return new Resolver<Iterable<String>>() {
            public Iterable<String> resolve(Type type) throws Exception {
                Parameters<String, String> parameters = (Parameters<String, String>) typeMap.resolve(parametersClass());
                if (!parameters.contains(name())) {
                    return sequence(defaultValueOrThrow());
                }
                return parameters.getValues(name());
            }
        };
    }

    public void addTo(Container container) {
        container.add(String.class, extractValueFrom(container)).
                add(new TypeFor<Iterable<String>>() {}.get(), extractValuesFrom(container));
    }
}
