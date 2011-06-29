package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Option;
import com.googlecode.yadic.Resolver;
import com.googlecode.yadic.TypeMap;
import com.googlecode.yadic.generics.TypeFor;

import java.lang.reflect.Type;

import static com.googlecode.totallylazy.Callables.callThrows;
import static com.googlecode.totallylazy.Sequences.sequence;

public class NamedParameter  {
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

    public String defaultValue() {
        return defaultValue.getOrElse(callThrows(new IllegalArgumentException(), String.class));
    }

    public Class<? extends Parameters<String, String>> parametersClass() {
        return parametersClass;
    }

    public Resolver<String> extractValueFrom(final TypeMap typeMap) {
        return new Resolver<String>() {
            public String resolve(Type type) throws Exception {
                Parameters<String, String> parameters = (Parameters<String, String>) typeMap.resolve(parametersClass());
                if(!parameters.contains(name())){
                    return defaultValue();
                }
                return parameters.getValue(name());
            }
        };
    }

    public Resolver<Iterable<String>> extractValuesFrom(final TypeMap typeMap) {
        return new Resolver<Iterable<String>>() {
            public Iterable<String> resolve(Type type) throws Exception {
                Parameters<String, String> parameters = (Parameters<String, String>) typeMap.resolve(parametersClass());
                if(!parameters.contains(name())){
                    return sequence(defaultValue());
                }
                return parameters.getValues(name());
            }
        };
    }

    public TypeMap addTo(TypeMap typeMap) {
        return typeMap.add(String.class, extractValueFrom(typeMap)).
                add(new TypeFor<Iterable<String>>() {}.get(), extractValuesFrom(typeMap));

    }
}
