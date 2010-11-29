package com.googlecode.utterlyidle;

import com.googlecode.yadic.Resolver;

import java.lang.reflect.InvocationTargetException;

public interface Activator {
    float matchQuality(Request request);

    int numberOfArguments();

    ResponseBody activate(Resolver container, Request request) throws InvocationTargetException, IllegalAccessException;

    int priority();
}
