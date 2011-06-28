package com.googlecode.utterlyidle.annotations;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Predicate;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

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
        return this.<T>getValue(annotation);
    }

    public static <T> T getValue(Annotation annotation) {
        try {
            return (T) annotation.getClass().getMethod(METHOD_NAME).invoke(annotation);
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public Annotation annotation() {
        return annotation;
    }
}