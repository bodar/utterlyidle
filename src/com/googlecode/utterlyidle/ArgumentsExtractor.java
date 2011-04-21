package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.*;
import com.googlecode.utterlyidle.annotations.Param;
import com.googlecode.utterlyidle.cookies.CookieParameters;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.generics.TypeFor;
import com.googlecode.yadic.resolvers.ProgrammerErrorResolver;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Sequences.sequence;
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

    public Object[] extract(final Request request) {
        return extract(request, sequence(method.getGenericParameterTypes()).zip(convertToAnnotationsToNamedParameter()));
    }

    private Object[] extract(final Request request, final Sequence<Pair<Type, Option<NamedParameter>>> typesWithNamedParameter) {
        return typesWithNamedParameter.map(new Callable1<Pair<Type, Option<NamedParameter>>, Object>() {
            public Object call(Pair<Type, Option<NamedParameter>> pair) throws Exception {
                final Type type = pair.first();
                final Option<NamedParameter> optionalParameter = pair.second();

                return application.usingArgumentScope(request, new Callable1<Container, Object>() {
                    public Object call(Container container) throws Exception {
                        container.addInstance(UriTemplate.class, uriTemplate);

                        final Type iterableStringType = new TypeFor<Iterable<String>>() {
                        }.get();

                        for (NamedParameter namedParameter : optionalParameter) {
                            container.add(String.class, namedParameter.extractValueFrom(container)).
                                    add(iterableStringType, namedParameter.extractValuesFrom(container));
                        }

                        if (!container.contains(String.class)) {
                            container.add(String.class, new ProgrammerErrorResolver(String.class));
                        }
                        if (!container.contains(iterableStringType)) {
                            container.add(iterableStringType, new ProgrammerErrorResolver(iterableStringType));
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

    private Sequence<Option<NamedParameter>> convertToAnnotationsToNamedParameter() {
        return sequence(method.getParameterAnnotations()).map(new Callable1<Annotation[], Option<NamedParameter>>() {
            public Option<NamedParameter> call(Annotation[] annotations) throws Exception {
                for (final Param param : sequence(annotations).map(toParam())) {
                    if (param.annotation() instanceof QueryParam) {
                        return some(new NamedParameter(param.<String>value(), QueryParameters.class));
                    }
                    if (param.annotation() instanceof FormParam) {
                        return some(new NamedParameter(param.<String>value(), FormParameters.class));

                    }
                    if (param.annotation() instanceof PathParam) {
                        return some(new NamedParameter(param.<String>value(), PathParameters.class));
                    }
                    if (param.annotation() instanceof HeaderParam) {
                        return some(new NamedParameter(param.<String>value(), HeaderParameters.class));
                    }
                    if (param.annotation() instanceof CookieParam) {
                        return some(new NamedParameter(param.<String>value(), CookieParameters.class));
                    }
                }
                return none();
            }
        });
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


    private static Callable2<? super Container, ? super Param, Container> with(final Class<? extends Parameters<String, String>> paramsClass) {
        return new Callable2<Container, Param, Container>() {
            public Container call(final Container container, final Param param) throws Exception {
                return container.addActivator(String.class, new Callable<String>() {
                    public String call() throws Exception {
                        Parameters<String, String> params = container.get(paramsClass);
                        if (!params.contains(param.<String>value())) {
                            throw new IllegalArgumentException();
                        }
                        return params.getValue(param.<String>value());
                    }
                });
            }
        };
    }
}