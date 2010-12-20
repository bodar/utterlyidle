package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.OptionActivator;
import com.googlecode.yadic.SimpleContainer;

import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

    public Object[] extract(final Request request) {
        Sequence<Pair<Type, Annotation[]>> parametersWithAnnotations = sequence(method.getGenericParameterTypes()).
                zip(sequence(method.getParameterAnnotations()));

        return parametersWithAnnotations.map(new Callable1<Pair<Type, Annotation[]>, Object>() {
            public Object call(Pair<Type, Annotation[]> pair) throws Exception {
                Type type = pair.first();
                Sequence<Annotation> annotations = sequence(pair.second()).filter(isParam());

                final Container container = createContainer(request);
                Class<?> aClass = getClassFrom(type);
                annotations.safeCast(QueryParam.class).map(toParam()).foldLeft(container, with(QueryParameters.class));
                annotations.safeCast(FormParam.class).map(toParam()).foldLeft(container, with(FormParameters.class));
                annotations.safeCast(PathParam.class).map(toParam()).foldLeft(container, with(PathParameters.class));
                annotations.safeCast(HeaderParam.class).map(toParam()).foldLeft(container, with(HeaderParameters.class));

                if (!container.contains(aClass) && !aClass.equals(Object.class)) {
                    addActivator(type, container);
                }

                return container.resolve(aClass);
            }
        }).toArray(Object.class);
    }

    private void addActivator(Type type, final Container container) {
        Class<?> aClass = getClassFrom(type);

        if (aClass.equals(Option.class)) {
            addOptionType(type, container);
        } else if (aClass.equals(Either.class)) {
            addEitherType(type, container);
        } else {
            addActualType(aClass, container);
        }
    }

    private void addOptionType(Type type, Container container) {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        final Class<?> typeClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        addActualType(typeClass, container);
        container.addActivator(Option.class, new OptionActivator(typeClass, container));
    }

    private void addEitherType(Type type, Container container) {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        final Class<?> leftClass = (Class<?>) actualTypeArguments[0];
        final Type rightClass = actualTypeArguments[1];
        addActivator(rightClass, container);
        container.addActivator(Either.class, new EitherActivator(leftClass, getClassFrom(rightClass), container));
    }

    private <T> void addActualType(Class<T> aClass, Container container) {
        if (aClass.getConstructors().length == 0) {
            container.addActivator(aClass, new StaticMethodActivator<T>(aClass, container, String.class));
        } else {
            container.add(aClass);
        }
    }

    private Class<?> getClassFrom(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class<?>) parameterizedType.getRawType();
        }
        throw new UnsupportedOperationException(type.toString());
    }

    private Callable2<? super Container, ? super Param, Container> with(final Class<? extends Parameters> paramsClass) {
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

    public Container createContainer(Request request) {
        Container container = new SimpleContainer();
        container.addInstance(Request.class, request);
        container.addInstance(UriTemplate.class, uriTemplate);
        container.addInstance(PathParameters.class, uriTemplate.extract(request.url().path().toString()));
        container.addInstance(HeaderParameters.class, request.headers());
        container.addInstance(QueryParameters.class, request.query());
        container.addInstance(FormParameters.class, request.form());
        container.addInstance(InputStream.class, request.input());
        return container;
    }

}