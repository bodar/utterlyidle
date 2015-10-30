package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.predicates.Predicate;
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
    private final ExceptionLogger logger;

    public ParametersExtractor(UriTemplate uriTemplate, Application application, Sequence<Pair<Type, Option<Parameter>>> typesWithNamedParameter, ExceptionLogger logger) {
        this.uriTemplate = uriTemplate;
        this.application = application;
        this.typesWithNamedParameter = typesWithNamedParameter;
        this.logger = logger;
    }

    public boolean matches(Request request) {
        try {
            extract(request);
            return true;
        } catch (Exception e) {
            logger.log(e);
            return false;
        }
    }

    public Object[] extract(final Request request) {
        return typesWithNamedParameter.
                map(pair -> application.usingArgumentScope(request, resolveParameter(pair))).
                toArray(Object.class);
    }

    private Function1<Container, Object> resolveParameter(final Pair<Type, Option<Parameter>> pair) {
        return container -> {
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

    public static Predicate<Binding> parametersMatches(final Request request, final Application application, final ExceptionLogger logger) {
        return binding -> new ParametersExtractor(binding.uriTemplate(), application, binding.parameters(), logger).matches(request);
    }

}