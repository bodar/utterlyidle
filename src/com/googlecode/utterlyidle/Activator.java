package com.googlecode.utterlyidle;

import com.googlecode.yadic.Resolver;

public interface Activator {
    float matchQuality(Request request);

    int numberOfArguments();

    Response activate(Resolver resolver, Request request) throws Exception;

    int priority();
}
