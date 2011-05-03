package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;
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
                header(HttpHeaders.CONTENT_TYPE, httpSignature.produces()).
                entity(result).
                status(Status.OK);
    }

    private Object getResponse(Request request, Object instance, Application application) throws Exception {
        try {
            Object[] arguments = new ParametersExtractor(httpSignature().uriTemplate(), application, httpSignature().arguments()).extract(request);
            return method.invoke(instance, arguments);
        } catch (InvocationTargetException e) {
            throw toException(e.getCause());
        }
    }

    @Override
    public String toString() {
        return method.getDeclaringClass().getSimpleName() + "." + method.getName() + Sequences.sequence(method.getGenericParameterTypes()).toString("(", ", ", ")");
    }

    public Method method() {
        return method;
    }
}