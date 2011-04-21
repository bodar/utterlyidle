package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.*;
import com.googlecode.utterlyidle.annotations.Param;
import com.googlecode.utterlyidle.cookies.CookieParameters;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.resolvers.ProgrammerErrorResolver;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.annotations.Param.isParam;
import static com.googlecode.utterlyidle.annotations.Param.toParam;
import static com.googlecode.yadic.resolvers.Resolvers.create;

public class ArgumentsExtractor implements RequestExtractor<Object[]> {
    private final Method method;
    private final UriTemplate uriTemplate;
    private final Application application;

    public ArgumentsExtractor(Method method, UriTemplate uriTemplate, Application application) {
        this.method = method;
        this.uriTemplate = uriTemplate;
        this.application = application;
    }

    public boolean matches(Request request) {
        try {
            extract(request);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static <T extends Parameters<String, String>> String extractParam(Container container, Param param, Class<T> aClass) {
        T params = container.get(aClass);
        if (!params.contains(param.<String>value())) {
            throw new IllegalArgumentException();
        }
        return params.getValue(param.<String>value());
    }

    public Object[] extract(final Request request) {
        Sequence<Pair<Type, Annotation[]>> parametersWithAnnotations = sequence(method.getGenericParameterTypes()).
                zip(sequence(method.getParameterAnnotations()));

        return parametersWithAnnotations.map(new Callable1<Pair<Type, Annotation[]>, Object>() {
            public Object call(Pair<Type, Annotation[]> pair) throws Exception {
                final Type type = pair.first();
                final Sequence<Annotation> annotations = sequence(pair.second()).filter(isParam());

                return application.usingArgumentScope(request, new Callable1<Container, Object>() {
                    public Object call(Container container) throws Exception {
                        container.addInstance(UriTemplate.class, uriTemplate);

                        annotations.safeCast(QueryParam.class).map(toParam()).foldLeft(container, with(QueryParameters.class));
                        annotations.safeCast(FormParam.class).map(toParam()).foldLeft(container, with(FormParameters.class));
                        annotations.safeCast(PathParam.class).map(toParam()).foldLeft(container, with(PathParameters.class));
                        annotations.safeCast(HeaderParam.class).map(toParam()).foldLeft(container, with(HeaderParameters.class));
                        annotations.safeCast(CookieParam.class).map(toParam()).foldLeft(container, with(CookieParameters.class));

                        if (!container.contains(String.class)) {
                            container.add(String.class, new ProgrammerErrorResolver(String.class));
                        }

                        List<Type> types = typeArgumentsOf(type);

                        for (Type t : types) {
                            if (!container.contains(t)) {
                                container.add(t, create(t, container));
                            }
                        }

                        return container.resolve(type);
                    }
                });
            }
        }).toArray(Object.class);
    }

    public static List<Type> typeArgumentsOf(Type type) {
        List<Type> types = new ArrayList<Type>();
        if (type instanceof ParameterizedType) {
            for (Type subType : ((ParameterizedType) type).getActualTypeArguments()) {
                types.addAll(typeArgumentsOf(subType));
            }
            return types;
        }

        if (type instanceof Class) {
            types.add(type);
            return types;
        }

        throw new UnsupportedOperationException("Does not support " + type.toString());
    }


    private static Callable2<? super Container, ? super Param, Container> with(final Class<? extends Parameters> paramsClass) {
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
}