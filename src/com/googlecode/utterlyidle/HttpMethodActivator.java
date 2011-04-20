package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.utterlyidle.annotations.Matchers;
import com.googlecode.yadic.Resolver;

import javax.ws.rs.core.HttpHeaders;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Exceptions.toException;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.yadic.resolvers.Resolvers.create;
import static com.googlecode.yadic.resolvers.Resolvers.resolve;

public class HttpMethodActivator implements Activator {
    private final Method method;
    private final UriTemplate uriTemplate;
    private final String httpMethod;
    private final Predicate<Request> consumesMatcher;
    private final ProducesMimeMatcher producesMatcher;
    private final RequestExtractor<Object[]> argumentsExtractor;
    private final int priority;

    public HttpMethodActivator(Method method, UriTemplate uriTemplate, String httpMethod, Predicate<Request> consumesMatcher, ProducesMimeMatcher producesMatcher, RequestExtractor<Object[]> argumentsExtractor, int priority) {
        this.method = method;
        this.uriTemplate = uriTemplate;
        this.httpMethod = httpMethod;
        this.consumesMatcher = consumesMatcher;
        this.producesMatcher = producesMatcher;
        this.argumentsExtractor = argumentsExtractor;
        this.priority = priority;
    }


    public String httpMethod() {
        return httpMethod;
    }

    public float matchQuality(Request request) {
        return producesMatcher.matchQuality(request);
    }

    public int numberOfArguments() {
        return method.getParameterTypes().length;
    }

    public Response activate(Resolver resolver, Request request) throws Exception {
        Class<?> declaringClass = method.getDeclaringClass();
        Object instance = resolve(create(declaringClass, resolver), declaringClass);
        Object result = getResponse(request, instance);
        if (result instanceof Response) {
            return (Response) result;
        }
        if (result instanceof Either) {
            result = ((Either) result).value();
        }

        return response().
                header(HttpHeaders.CONTENT_TYPE, producesMatcher.mimeType()).
                entity(result).
                status(Status.OK);
    }

    private Object getResponse(Request request, Object instance) throws Exception {
        try {
            return method.invoke(instance, argumentsExtractor.extract(request));
        } catch (InvocationTargetException e) {
            throw toException(e.getCause());
        }
    }

    public int priority() {
        return priority;
    }

    public Predicate<Request> pathMatcher(BasePath basePath) {
        return new PathMatcher(basePath, uriTemplate);
    }

    public UriTemplate uriTemplate() {
        return uriTemplate;
    }

    public Predicate<Request> methodMatcher() {
        return new MethodMatcher(httpMethod);
    }

    public Predicate<Request> consumesMatcher() {
        return consumesMatcher;
    }

    @Override
    public String toString() {
        return method.getDeclaringClass().getSimpleName() + "." + method.getName() + Sequences.sequence(method.getGenericParameterTypes()).toString("(", ", ", ")");
    }

    public Predicate<Request> producesMatcher() {
        return producesMatcher;
    }

    public Predicate<Request> argumentMatcher() {
        return argumentsExtractor;
    }

    public Method method() {
        return method;
    }
}