package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.yadic.Resolver;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Sequences.sequence;
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

    public void activate(Resolver container, Request request, Response response) {
        try {
            Object instance = container.resolve(method.getDeclaringClass());
            Object result = method.invoke(instance, argumentsExtractor.extract(request));
            response.header(HttpHeaders.CONTENT_TYPE, producesMatcher.mimeType());
            if (result == null) {
                response.code(NO_CONTENT);
            }
            if (result instanceof Redirect) {
                ((Redirect) result).applyTo(request.base(), response);
            }
            if (result instanceof String) {
                response.write((String) result);
            }
            if (result instanceof StreamingOutput) {
                ((StreamingOutput) result).write(response.output());
            }
            if (result instanceof StreamingWriter) {
                ((StreamingWriter) result).write(response.writer());
            }
        } catch (InvocationTargetException e1) {
            throw new RuntimeException(e1.getCause());
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        } catch (IllegalAccessException e1) {
            throw new RuntimeException(e1);
        }
    }
}