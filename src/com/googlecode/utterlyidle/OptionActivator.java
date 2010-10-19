package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Option;
import com.googlecode.yadic.Container;

import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;

public class OptionActivator implements Callable<Option> {
    private final Container container;
    private final Class<?> typeClass;

    public OptionActivator(Container container, Class<?> typeClass) {
        this.container = container;
        this.typeClass = typeClass;
    }

    public Option call() throws Exception {
        try {
            return some(container.get(typeClass));
        } catch (NoSuchElementException e) {
            return none();
        }
    }
}
