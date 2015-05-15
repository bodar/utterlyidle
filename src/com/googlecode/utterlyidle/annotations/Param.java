package com.googlecode.utterlyidle.annotations;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Predicate;

import java.lang.annotation.Annotation;

import static com.googlecode.totallylazy.Unchecked.cast;

public class Param {
    private final Annotation annotation;
    private static final String METHOD_NAME = "value";

    private Param(Annotation annotation) {
        this.annotation = annotation;
    }

    public static Predicate<Annotation> isParam() {
        return new Predicate<Annotation>() {
            public boolean matches(Annotation annotation) {
                return isParam(annotation);
            }
        };
    }

    public static boolean isParam(Annotation annotation) {
        try {
            return annotation.getClass().getMethod(METHOD_NAME) != null;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static Callable1<Annotation, Param> toParam() {
        return new Callable1<Annotation, Param>() {
            public Param call(Annotation annotation) throws Exception {
                return param(annotation);
            }
        };
    }

    public static Param param(Annotation annotation) {
        if (!isParam(annotation)) {
            throw new IllegalArgumentException("annotation");
        }
        return new Param(annotation);
    }

    public <T> T value() {
        return getValue(annotation);
    }

    public static <T> T getValue(Annotation annotation) {
        try {
            return cast(annotation.getClass().getMethod(METHOD_NAME).invoke(annotation));
        } catch (ReflectiveOperationException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public Annotation annotation() {
        return annotation;
    }

    public static <T> Callable1<? super Annotation, T> toValue() {
        return new Callable1<Annotation, T>() {
            public T call(Annotation annotation) throws Exception {
                return cast(getValue(annotation));
            }
        };
    }
}