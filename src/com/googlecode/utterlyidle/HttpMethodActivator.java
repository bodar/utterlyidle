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
    private final HttpSignature httpSignature;
    private final Method method;

    public HttpMethodActivator(HttpSignature httpSignature, Method method) {
        this.httpSignature = httpSignature;
        this.method = method;
    }

    public HttpSignature httpSignature() {
        return httpSignature;
    }

    public float matchQuality(Request request) {
        return producesMatcher().matchQuality(request);
    }


    public Response activate(Resolver resolver, Request request, Application application) throws Exception {
        Class<?> declaringClass = method.getDeclaringClass();
        Object instance = resolve(create(declaringClass, resolver), declaringClass);
        Object result = getResponse(request, instance, application);
        if (result instanceof Response) {
            return (Response) result;
        }
        if (result instanceof Either) {
            result = ((Either) result).value();
        }

        return response().
                header(HttpHeaders.CONTENT_TYPE, producesMatcher().mimeType()).
                entity(result).
                status(Status.OK);
    }

    private Object getResponse(Request request, Object instance, Application application) throws Exception {
        try {
            Object[] arguments = parameterExtractor(application).extract(request);
            return method.invoke(instance, arguments);
        } catch (InvocationTargetException e) {
            throw toException(e.getCause());
        }
    }

    private ParametersExtractor parameterExtractor(Application application) {
        return new ParametersExtractor(httpSignature().uriTemplate(), application, httpSignature().arguments());
    }

    public Predicate<Request> pathMatcher(BasePath basePath) {
        return new PathMatcher(basePath, httpSignature().uriTemplate());
    }

    public Predicate<Request> methodMatcher() {
        return new MethodMatcher(httpSignature().httpMethod());
    }

    public Predicate<Request> consumesMatcher() {
        return new ConsumesMimeMatcher(httpSignature().consumes());
    }

    @Override
    public String toString() {
        return method.getDeclaringClass().getSimpleName() + "." + method.getName() + Sequences.sequence(method.getGenericParameterTypes()).toString("(", ", ", ")");
    }

    public ProducesMimeMatcher producesMatcher() {
        return new ProducesMimeMatcher(httpSignature().produces());
    }

    public Predicate<Request> parameterMatcher(Application application) {
        return parameterExtractor(application);
    }

    public Method method() {
        return method;
    }
}