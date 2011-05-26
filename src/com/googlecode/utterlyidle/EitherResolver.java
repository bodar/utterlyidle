package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.None;
import com.googlecode.totallylazy.Option;
import com.googlecode.yadic.Resolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Left.left;
import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Right.right;

public class EitherResolver implements Resolver<Either> {
    private final Resolver resolver;

    public EitherResolver(Resolver resolver) {
        this.resolver = resolver;
    }

    public Either resolve(Type type) throws Exception {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        final Type leftClass = actualTypeArguments[0];
        final Type rightClass = actualTypeArguments[1];

        try {
            return right(resolver.resolve(rightClass));
        } catch (Exception e) {
            return left(resolver.resolve(leftClass));
        }
    }
}
