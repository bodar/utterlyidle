package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.yadic.Resolver;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.googlecode.totallylazy.Callables.callThrows;
import static com.googlecode.totallylazy.Sequences.sequence;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

public class RestEngine implements Engine {
    List<HttpMethodActivator> activators = new ArrayList<HttpMethodActivator>();

    public void add(Class resource) {
        for (final Method method : resource.getMethods()) {
            for (final HttpMethod httpMethod : getHttpMethod(method)) {
                activators.add(new HttpMethodActivator(httpMethod.value(), method));
            }
        }
    }

    public Option<HttpMethod> getHttpMethod(Method method) {
        return sequence(method.getAnnotations()).tryPick(new Callable1<Annotation, Option<HttpMethod>>() {
            public Option<HttpMethod> call(Annotation annotation) throws Exception {
                return sequence(annotation.annotationType()).safeCast(HttpMethod.class).headOption();
            }
        });
    }

    public void handle(Resolver container, Request request, Response response) {
        findActivator(request).get().activate(container, request, response);
    }

    public Option<HttpMethodActivator> findActivator(final Request request) {
        return sequence(activators).filter(new Predicate<HttpMethodActivator>() {
            public boolean matches(HttpMethodActivator httpMethodActivator) {
                return httpMethodActivator.matches(request);
            }
        }).sortBy(matchQuality(request)).headOption();
    }

    private Comparator<? super HttpMethodActivator> matchQuality(final Request request) {
        return new Comparator<HttpMethodActivator>() {
            public int compare(HttpMethodActivator first, HttpMethodActivator second) {
                float firstQuality = first.matchQuality(request);
                float secondQuality = second.matchQuality(request);
                if (firstQuality == secondQuality)
                    return first.numberOfArguments() - second.numberOfArguments();
                return (int) (firstQuality - secondQuality);
            }
        };
    }
}