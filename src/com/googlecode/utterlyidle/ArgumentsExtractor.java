package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.*;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.SimpleContainer;
import com.googlecode.yadic.generics.TypeFor;
import com.googlecode.yadic.resolvers.OptionResolver;

import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.Param.isParam;
import static com.googlecode.utterlyidle.Param.toParam;
import static com.googlecode.yadic.generics.Types.typeArgumentsOf;
import static com.googlecode.yadic.resolvers.Resolvers.create;

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
            throw new IllegalArgumentException();
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
                annotations.safeCast(QueryParam.class).map(toParam()).foldLeft(container, with(QueryParameters.class));
                annotations.safeCast(FormParam.class).map(toParam()).foldLeft(container, with(FormParameters.class));
                annotations.safeCast(PathParam.class).map(toParam()).foldLeft(container, with(PathParameters.class));
                annotations.safeCast(HeaderParam.class).map(toParam()).foldLeft(container, with(HeaderParameters.class));

                List<Type> types = typeArgumentsOf(type);

                for (Type t : types) {
                    if(!container.contains(t)){
                        container.add(t, create(t, container));
                    }
                }

                return container.resolve(type);
            }
        }).toArray(Object.class);
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
        final Container container = new SimpleContainer();
        container.addInstance(Request.class, request);
        container.addInstance(UriTemplate.class, uriTemplate);
        container.addInstance(PathParameters.class, uriTemplate.extract(request.url().path().toString()));
        container.addInstance(HeaderParameters.class, request.headers());
        container.addInstance(QueryParameters.class, request.query());
        container.addInstance(FormParameters.class, request.form());
        container.addInstance(InputStream.class, request.input());
        container.add(new TypeFor<Option<?>>() {{}}.get(), new OptionResolver(container, instanceOf(IllegalArgumentException.class)));
        container.add(new TypeFor<Either<?, ?>>() {{}}.get(), new EitherResolver(container));
        return container;
    }

}