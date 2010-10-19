package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Option;
import com.googlecode.yadic.Container;

import java.util.concurrent.Callable;

public class OptionActivator implements Callable<Option> {
    private final Container container;
    private final Class<?> typeClass;

    public OptionActivator(Container container, Class<?> typeClass) {
        this.container = container;
        this.typeClass = typeClass;
    }

    public Option call() throws Exception {
        final Object instance = container.get(typeClass);
        return Option.option(instance);
    }
}
