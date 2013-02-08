package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.generics.TypeFor;
import com.googlecode.yadic.resolvers.ProgrammerErrorResolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.googlecode.yadic.resolvers.Resolvers.create;

public class ParametersExtractor implements RequestExtractor<Object[]> {
    private final UriTemplate uriTemplate;
    private final Application application;
    private final Sequence<Pair<Type, Option<Parameter>>> typesWithNamedParameter;

    public ParametersExtractor(UriTemplate uriTemplate, Application application, Sequence<Pair<Type, Option<Parameter>>> typesWithNamedParameter) {
        this.uriTemplate = uriTemplate;
        this.application = application;
        this.typesWithNamedParameter = typesWithNamedParameter;
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
        return typesWithNamedParameter.map(new Callable1<Pair<Type, Option<Parameter>>, Object>() {
            public Object call(Pair<Type, Option<Parameter>> pair) throws Exception {
                return application.usingArgumentScope(request, resolveParameter(pair));
            }
        }).toArray(Object.class);
    }

    private Callable1<Container, Object> resolveParameter(final Pair<Type, Option<Parameter>> pair) {
        return new Callable1<Container, Object>() {
            public Object call(Container container) throws Exception {
                final Type type = pair.first();
                final Option<Parameter> optionalParameter = pair.second();

                container.addInstance(UriTemplate.class, uriTemplate);


                for (Parameter parameter : optionalParameter) {
                    parameter.addTo(container);
                }

                if (!container.contains(String.class)) {
                    container.addType(String.class, new ProgrammerErrorResolver(String.class));
                }

                final Type iterableStringType = new TypeFor<Iterable<String>>() {}.get();
                if (!container.contains(iterableStringType)) {
                    container.addType(iterableStringType, new ProgrammerErrorResolver(iterableStringType));
                }

                List<Type> types = typeArgumentsOf(type);

                for (Type t : types) {
                    if (!container.contains(t)) {
                        container.addType(t, create(t, container));
                    }
                }

                return container.resolve(type);

            }
        };
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

    public static Predicate<Binding> parametersMatches(final Request request, final Application application) {
        return new Predicate<Binding>() {
            public boolean matches(Binding binding) {
                return new ParametersExtractor(binding.uriTemplate(), application, binding.parameters()).matches(request);
            }
        };
    }

}