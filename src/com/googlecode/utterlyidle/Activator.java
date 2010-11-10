package com.googlecode.utterlyidle;

import com.googlecode.yadic.Resolver;

public interface Activator {
    float matchQuality(Request request);

    int numberOfArguments();

    ResponseBody activate(Resolver container, Request request);

    int priority();
}
