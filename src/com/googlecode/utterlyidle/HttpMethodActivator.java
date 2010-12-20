package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Sequences;
import com.googlecode.yadic.Resolver;

import javax.ws.rs.core.HttpHeaders;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Callers.call;
import static com.googlecode.yadic.CreateCallable.create;

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

    public Response activate(Resolver resolver, Request request, Response response) throws InvocationTargetException, IllegalAccessException {
        Object instance = call(create(method.getDeclaringClass(), resolver));
        return response.
                header(HttpHeaders.CONTENT_TYPE, producesMatcher.mimeType()).
                entity(method.invoke(instance, argumentsExtractor.extract(request)));
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