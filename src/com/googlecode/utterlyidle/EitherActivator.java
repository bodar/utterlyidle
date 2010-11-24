package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.None;
import com.googlecode.totallylazy.Option;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;

import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Left.left;
import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Right.right;

public class EitherActivator implements Callable<Either> {
    private final Class<?> leftClass;
    private final Class<?> rightClass;
    private final Resolver resolver;

    public EitherActivator(Class<?> leftClass, Class<?> rightClass, Resolver resolver) {
        this.leftClass = leftClass;
        this.rightClass = rightClass;
        this.resolver = resolver;
    }

    public Either call() throws Exception {
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
