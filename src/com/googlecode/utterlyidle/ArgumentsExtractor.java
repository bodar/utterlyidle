package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.SimpleContainer;

import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.Param.isParam;
import static com.googlecode.utterlyidle.Param.toParam;

public class ArgumentsExtractor implements RequestExtractor<Object[]> {
    private final UriTemplate uriTemplate;
    private final Method method;

    public ArgumentsExtractor(UriTemplate uriTemplate, Method method) {
        this.uriTemplate = uriTemplate;
        this.method = method;
    }

    public boolean matches(Request request) {
        try {
            extract(request);
            return true;
        } catch (Exception e) {
            System.out.println("e = " + e);
            return false;
        }
    }

    public <T extends Parameters> String extractParam(Container container, Param param, Class<T> aClass) {
        T params = container.get(aClass);
        if (!params.contains(param.value())) {
            throw new NoSuchElementException();
        }
        return params.getValue(param.value());
    }

    public Object[] extract(Request request) {
        final Container container = getArgumentContainer(request);

        Sequence<Pair<Class<?>, Annotation[]>> parametersWithAnnotations = sequence(method.getParameterTypes()).
                zip(sequence(method.getParameterAnnotations()));

        return parametersWithAnnotations.map(new Callable1<Pair<Class<?>, Annotation[]>, Object>() {
            public Object call(Pair<Class<?>, Annotation[]> pair) throws Exception {
                Class<?> aClass = pair.first();
                Sequence<Annotation> annotations = sequence(pair.second()).filter(isParam());
                if (!container.contains(aClass)) {
                    container.add(aClass);
                }

                container.remove(String.class);
                annotations.safeCast(QueryParam.class).map(toParam()).foldLeft(container, into(QueryParameters.class));
                annotations.safeCast(FormParam.class).map(toParam()).foldLeft(container, into(FormParameters.class));
                annotations.safeCast(PathParam.class).map(toParam()).foldLeft(container, into(PathParameters.class));
                annotations.safeCast(HeaderParam.class).map(toParam()).foldLeft(container, into(HeaderParameters.class));
                return container.resolve(aClass);
            }
        }).toArray(Object.class);
    }

    private Callable2<? super Container, ? super Param, Container> into(final Class<? extends Parameters> paramsClass) {
        return new Callable2<Container, Param, Container>() {
            public Container call(final Container container, final Param param) throws Exception {
                return container.addActivator(String.class, new Callable<String>() {
                    public String call() throws Exception {
                        return extractParam(container, param, paramsClass);
                    }
                });
            }
        };
    }

    public Container getArgumentContainer(Request request) {
        Container container = new SimpleContainer();
        container.addInstance(Request.class, request);
        container.addInstance(UriTemplate.class, uriTemplate);
        container.addInstance(PathParameters.class, uriTemplate.extract(request.path()));
        container.addInstance(HeaderParameters.class, request.headers());
        container.addInstance(QueryParameters.class, request.query());
        container.addInstance(FormParameters.class, request.form());
        container.addInstance(InputStream.class, request.input());
        return container;
    }

}