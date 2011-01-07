package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.yadic.Resolver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Predicates.*;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.yadic.generics.Types.classOf;
import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.STATIC;

public class StaticMethodResolver<T> implements Resolver<T> {
    private final Resolver resolver;
    private final Class<?> argumentType;

    public StaticMethodResolver(Resolver resolver, Class<?> argumentType) {
        this.resolver = resolver;
        this.argumentType = argumentType;
    }

    public T resolve(Type type) throws Exception {
        final Class returnType = classOf(type);
        final Sequence<Method> methods = sequence(returnType.getMethods()).filter(modifier(PUBLIC).and(modifier(STATIC)).and(arguments(argumentType)));
        return methods.pick(new Callable1<Method, Option<T>>() {
            public Option<T> call(Method method) throws Exception {
                try {
                    return (Option<T>) some(returnType.cast(method.invoke(null, resolver.resolve(argumentType))));
                } catch (InvocationTargetException e) {
                    return none();
                }
            }
        });
    }
}
