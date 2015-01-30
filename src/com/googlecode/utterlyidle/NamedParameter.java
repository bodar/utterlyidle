package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Option;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;
import com.googlecode.yadic.TypeMap;
import com.googlecode.yadic.generics.TypeFor;

import java.lang.reflect.Type;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Callables.callThrows;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Unchecked.cast;

public class NamedParameter implements Parameter {
    private final String name;
    private final Option<String> defaultValue;
    private final Class<? extends Parameters<String, String, ?>> parametersClass;

    public NamedParameter(String name, Class<? extends Parameters<String, String, ?>> parametersClass, Option<String> defaultValue) {
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

    public Class<? extends Parameters<String, String, ?>> parametersClass() {
        return parametersClass;
    }

    public Callable<String> extractValueFrom(final TypeMap typeMap) {
        return new Callable<String>() {
            public String call() throws Exception {
                Parameters<String, String, ?> parameters = cast(typeMap.resolve(parametersClass()));
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
                Parameters<String, String, ?> parameters = cast(typeMap.resolve(parametersClass()));
                if (!parameters.contains(name())) {
                    return sequence(defaultValueOrThrow());
                }
                return parameters.getValues(name());
            }
        };
    }

    public void addTo(Container container) {
        container.addActivator(String.class, extractValueFrom(container)).
                addType(new TypeFor<Iterable<String>>() {}.get(), extractValuesFrom(container));
    }

    public static class methods {
        public static Callable1<NamedParameter, Option<String>> defaultValue() {
            return new Callable1<NamedParameter, Option<String>>() {
                @Override
                public Option<String> call(NamedParameter namedParameter) throws Exception {
                    return namedParameter.defaultValue();
                }
            };
        }
    }
}
