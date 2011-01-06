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

public class EitherActivator implements Resolver<Either> {
    private final Resolver resolver;

    public EitherActivator(Resolver resolver) {
        this.resolver = resolver;
    }

    public Either resolve(Type type) throws Exception {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        final Type leftClass = actualTypeArguments[0];
        final Type rightClass = actualTypeArguments[1];

        try {
            final Object leftValue = resolver.resolve(leftClass);
            try {
                final Object rightValue = resolver.resolve(rightClass);
                if(rightValue instanceof None) {
                    return left(leftValue);
                }
                return right(rightValue);
            } catch (Exception e) {
                return left(leftValue);
            }
        } catch (Exception e) {
            if(rightClass.equals(Option.class)){
                return right(none());
            }
            throw e;
        }
    }
}
