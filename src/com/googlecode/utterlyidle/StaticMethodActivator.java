package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.yadic.Resolver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Predicates.*;
import static com.googlecode.totallylazy.Sequences.sequence;
import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.STATIC;

public class StaticMethodActivator<T> implements Callable<T> {
    private final Class<T> returnType;
    private final Resolver resolver;
    private final Class<?> argumentType;

    public StaticMethodActivator(Class<T> returnType, Resolver resolver, Class<?> argumentType) {
        this.returnType = returnType;
        this.resolver = resolver;
        this.argumentType = argumentType;
    }

    public T call() throws Exception {
        final Sequence<Method> methods = sequence(returnType.getMethods()).filter(and(modifier(PUBLIC), modifier(STATIC), arguments(argumentType)));
        return methods.pick(new Callable1<Method, Option<T>>() {
            public Option<T> call(Method method) throws Exception {
                try {
                    return some(returnType.cast(method.invoke(null, resolver.resolve(argumentType))));
                } catch (InvocationTargetException e) {
                    return none();
                }
            }
        });
    }
}
