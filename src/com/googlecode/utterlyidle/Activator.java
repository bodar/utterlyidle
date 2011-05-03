package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicate;
import com.googlecode.yadic.Resolver;

import java.lang.reflect.Method;

public interface Activator {
    float matchQuality(Request request);

    int numberOfArguments();

    Response activate(Resolver resolver, Request request, Application application) throws Exception;

    int priority();

    Predicate<Request> pathMatcher(BasePath basePath);

    Predicate<Request>  methodMatcher();

    Predicate<Request>  consumesMatcher();

    Predicate<Request>  producesMatcher();

    Predicate<Request> parameterMatcher(Application application);

    Method method();

    String httpMethod();

    UriTemplate uriTemplate();
}
