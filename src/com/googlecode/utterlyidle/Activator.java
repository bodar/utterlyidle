package com.googlecode.utterlyidle;

import com.googlecode.yadic.Resolver;

import java.lang.reflect.InvocationTargetException;

public interface Activator {
    float matchQuality(Request request);

    int numberOfArguments();

    Response activate(Resolver resolver, Request request, Response response) throws InvocationTargetException, IllegalAccessException;

    int priority();
}
