package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequences;
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
    private final ArgumentsExtractor argumentsExtractor;
    private final ProducesMimeMatcher producesMatcher;
    private final MethodMatcher methodMatcher;
    private final ConsumesMimeMatcher consumesMatcher;
    private final PriorityExtractor priorityExtractor;
    private final UriTemplate uriTemplate;
    private final String httpMethod;

    public HttpMethodActivator(String httpMethod, Method method, Application application) {
        this.httpMethod = httpMethod;
        this.method = method;
        uriTemplate = new UriTemplateExtractor().extract(method);
        argumentsExtractor = new ArgumentsExtractor(method, uriTemplate, application);
        producesMatcher = new ProducesMimeMatcher(method);
        methodMatcher = new MethodMatcher(this.httpMethod);
        consumesMatcher = new ConsumesMimeMatcher(method);
        priorityExtractor = new PriorityExtractor(method);
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
            result = ((Either)result).value();
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
        return priorityExtractor.extract(method);
    }

    public Predicate<Request> pathMatcher(BasePath basePath) {
        return new PathMatcher(basePath, uriTemplate);
    }

    public UriTemplate uriTemplate() {
        return uriTemplate;
    }

    public Predicate<Request>  methodMatcher() {
        return methodMatcher;
    }

    public Predicate<Request>  consumesMatcher() {
        return consumesMatcher;
    }

    @Override
    public String toString() {
        return method.getDeclaringClass().getSimpleName() + "." + method.getName() + Sequences.sequence(method.getGenericParameterTypes()).toString("(", ", ", ")");
    }

    public Predicate<Request>  producesMatcher() {
        return producesMatcher;
    }

    public Predicate<Request>  argumentMatcher() {
        return argumentsExtractor;
    }

    public Method method() {
        return method;
    }
}