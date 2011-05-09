package com.googlecode.utterlyidle;

import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;
import com.googlecode.yadic.TypeMap;

import java.lang.reflect.Type;
import java.util.concurrent.Callable;

public class NamedParameter  {
    private final String name;
    private final Class<? extends Parameters<String, String>> parametersClass;

    public NamedParameter(String name, Class<? extends Parameters<String, String>> parametersClass) {
        this.name = name;
        this.parametersClass = parametersClass;
    }

    public String name() {
        return name;
    }

    public Class<? extends Parameters<String, String>> parametersClass() {
        return parametersClass;
    }

    public Resolver<String> extractValueFrom(final TypeMap typeMap) {
        return new Resolver<String>() {
            public String resolve(Type type) throws Exception {
                Parameters<String, String> parameters = (Parameters<String, String>) typeMap.resolve(parametersClass());
                if(!parameters.contains(name())){
                    throw new IllegalArgumentException();
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
                    throw new IllegalArgumentException();
                }
                return parameters.getValues(name());
            }
        };
    }
}
