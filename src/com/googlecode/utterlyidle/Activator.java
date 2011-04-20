package com.googlecode.utterlyidle;

import com.googlecode.yadic.Resolver;

import java.lang.reflect.Method;

public interface Activator {
    float matchQuality(Request request);

    int numberOfArguments();

    Response activate(Resolver resolver, Request request) throws Exception;

    int priority();

    PathMatcher pathMatcher(BasePath basePath);

    MethodMatcher methodMatcher();

    ConsumesMimeMatcher consumesMatcher();

    ProducesMimeMatcher producesMatcher();

    ArgumentsExtractor argumentMatcher();

    Method method();
}
