package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Sequences;
import com.googlecode.yadic.Resolver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.googlecode.utterlyidle.ResponseBody.responseBody;

public class HttpMethodActivator implements Activator {
    private final Method method;
    private final ArgumentsExtractor argumentsExtractor;
    private final ProducesMimeMatcher producesMatcher;
    private final PathMatcher pathMatcher;
    private final MethodMatcher methodMatcher;
    private final ConsumesMimeMatcher consumesMatcher;
    private final PriorityExtractor priorityExtractor;

    public HttpMethodActivator(String httpMethod, Method method) {
        this.method = method;
        UriTemplate uriTemplate = new UriTemplateExtractor().extract(method);
        argumentsExtractor = new ArgumentsExtractor(uriTemplate, method);
        producesMatcher = new ProducesMimeMatcher(method);
        pathMatcher = new PathMatcher(uriTemplate);
        methodMatcher = new MethodMatcher(httpMethod);
        consumesMatcher = new ConsumesMimeMatcher(method);
        priorityExtractor = new PriorityExtractor(method);
    }

    public float matchQuality(Request request) {
        return producesMatcher.matchQuality(request);
    }

    public int numberOfArguments() {
        return method.getParameterTypes().length;
    }

    public ResponseBody activate(Resolver container, Request request) {
        try {
            Object instance = container.resolve(method.getDeclaringClass());
            return responseBody(producesMatcher.mimeType(), method.invoke(instance, argumentsExtractor.extract(request)));
        } catch (InvocationTargetException e) {
             return responseBody(producesMatcher.mimeType(), e.getCause());
        } catch (IllegalAccessException e) {
            return responseBody(producesMatcher.mimeType(), e);
        }
    }

    public int priority() {
        return priorityExtractor.extract(method);
    }

    public PathMatcher pathMatcher() {
        return pathMatcher;
    }

    public MethodMatcher methodMatcher() {
        return methodMatcher;
    }

    public ConsumesMimeMatcher consumesMatcher() {
        return consumesMatcher;
    }

    @Override
    public String toString() {
        return method.getDeclaringClass().getSimpleName() + "." + method.getName() + Sequences.sequence(method.getGenericParameterTypes()).toString("(", ", ", ")");
    }

    public ProducesMimeMatcher producesMatcher() {
        return producesMatcher;
    }

    public ArgumentsExtractor argumentMatcher() {
        return argumentsExtractor;
    }

    public Method method() {
        return method;
    }
}