package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.yadic.Resolver;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.ResponseBody.responseBody;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

public class HttpMethodActivator implements Predicate<Request> {
    private final Method method;
    private final ArgumentsExtractor argumentsExtractor;
    private final ProducesMimeMatcher producesMatcher;
    private final Sequence<Predicate<Request>> matchers;

    public HttpMethodActivator(String httpMethod, Method method) {
        this.method = method;
        UriTemplate uriTemplate = new UriTemplateExtractor().extract(method);
        argumentsExtractor = new ArgumentsExtractor(uriTemplate, method);
        producesMatcher = new ProducesMimeMatcher(method);
        matchers = sequence(new MethodMatcher(httpMethod), new PathMatcher(uriTemplate), producesMatcher,
                new ConsumesMimeMatcher(method), argumentsExtractor);
    }

    public boolean matches(final Request request) {
        return matchers.forAll(new Predicate<Predicate<Request>>() {
            public boolean matches(Predicate<Request> predicate) {
                System.out.println("predicate = " + predicate);
                boolean result = predicate.matches(request);
                System.out.println("result = " + result);
                return result;
            }
        });
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
        } catch (InvocationTargetException e1) {
            throw new RuntimeException(e1.getCause());
        } catch (IllegalAccessException e1) {
            throw new RuntimeException(e1);
        }
    }
}