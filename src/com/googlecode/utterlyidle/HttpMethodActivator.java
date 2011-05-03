package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.yadic.Resolver;

import javax.ws.rs.core.HttpHeaders;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static com.googlecode.totallylazy.Exceptions.toException;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.yadic.resolvers.Resolvers.create;
import static com.googlecode.yadic.resolvers.Resolvers.resolve;

public class HttpMethodActivator implements Activator {
    private final Method method;
    private final UriTemplate uriTemplate;
    private final String httpMethod;
    private final String consumes;
    private final String produces;
    private final Sequence<Pair<Type, Option<NamedParameter>>> arguments;
    private final int priority;

    public HttpMethodActivator(Method method, UriTemplate uriTemplate, String httpMethod, String consumes, String produces, Sequence<Pair<Type, Option<NamedParameter>>> arguments, int priority) {
        this.method = method;
        this.uriTemplate = uriTemplate;
        this.httpMethod = httpMethod;
        this.consumes = consumes;
        this.produces = produces;
        this.arguments = arguments;
        this.priority = priority;
    }

    public String httpMethod() {
        return httpMethod;
    }

    public float matchQuality(Request request) {
        return producesMatcher().matchQuality(request);
    }

    public int numberOfArguments() {
        return method.getParameterTypes().length;
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
        return new ParametersExtractor(uriTemplate, application, arguments);
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
        return new ConsumesMimeMatcher(consumes);
    }

    @Override
    public String toString() {
        return method.getDeclaringClass().getSimpleName() + "." + method.getName() + Sequences.sequence(method.getGenericParameterTypes()).toString("(", ", ", ")");
    }

    public ProducesMimeMatcher producesMatcher() {
        return new ProducesMimeMatcher(produces);
    }

    public Predicate<Request> parameterMatcher(Application application) {
        return parameterExtractor(application);
    }

    public Method method() {
        return method;
    }
}