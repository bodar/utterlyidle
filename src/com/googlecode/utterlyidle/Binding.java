package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.*;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static com.googlecode.totallylazy.Predicates.some;
import static com.googlecode.utterlyidle.NamedParameter.methods.defaultValue;

public class Binding {
    private final Method method;
    private final String httpMethod;
    private final UriTemplate uriTemplate;
    private final Sequence<String> consumes;
    private final Sequence<String> produces;
    private final Sequence<Pair<Type, Option<Parameter>>> parameters;
    private final int priority;
    private final boolean hidden;

    public Binding(Method method, UriTemplate uriTemplate, String httpMethod, Sequence<String> consumes, Sequence<String> produces, Sequence<Pair<Type, Option<Parameter>>> parameters, int priority, boolean hidden) {
        this.method = method;
        this.uriTemplate = uriTemplate;
        this.httpMethod = httpMethod;
        this.consumes = consumes;
        this.produces = produces;
        this.parameters = parameters.realise();
        this.priority = priority;
        this.hidden = hidden;
    }

    public Method method() {
        return method;
    }

    public String httpMethod() {
        return httpMethod;
    }

    public UriTemplate uriTemplate() {
        return uriTemplate;
    }

    public Sequence<String> consumes() {
        return consumes;
    }

    public Sequence<String> produces() {
        return produces;
    }

    public Sequence<Pair<Type, Option<Parameter>>> parameters() {
        return parameters;
    }

    @SuppressWarnings("unchecked")
    public Sequence<NamedParameter> namedParameters() {
        return parameters.map(Callables.<Option<Parameter>>second()).
                filter(Predicates.<Parameter>some()).
                map(Callables.<Parameter>value()).safeCast(NamedParameter.class);
    }

    public int numberOfArguments() {
        return parameters.size();
    }

    private int numberOfDefaultArguments() {
        return namedParameters().map(defaultValue()).filter(Predicates.<String>some()).size();
    }

    public int priority() {
        return priority;
    }

    public boolean hidden() {
        return hidden;
    }

    @Override
    public int hashCode() {
        return myFields().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Binding && myFields().equals(((Binding) obj).myFields());
    }

    protected Sequence myFields() {
        return Sequences.sequence(method, httpMethod, uriTemplate, consumes, produces, parameters, priority);
    }

    @Override
    public String toString() {
        return String.format("%s %s -> %s", httpMethod, uriTemplate, method);
    }

    public static Callable1<Binding, Method> extractMethod() {
        return new Callable1<Binding, Method>() {
            @Override
            public Method call(final Binding binding) throws Exception {
                return binding.method();
            }
        };
    }
    public static class functions {
        public static Function1<Binding, Integer> priority() {
            return new Function1<Binding, Integer>() {
                @Override
                public Integer call(Binding binding) throws Exception {
                    return binding.priority();
                }
            };
        }

        public static Function1<Binding, Integer> numberOfArguments() {
            return new Function1<Binding, Integer>() {
                @Override
                public Integer call(Binding binding) throws Exception {
                    return binding.numberOfArguments();
                }
            };
        }

        public static Function1<Binding, Integer> numberOfDefaultArguments() {
            return new Function1<Binding, Integer>() {
                @Override
                public Integer call(Binding binding) throws Exception {
                    return binding.numberOfDefaultArguments();
                }
            };
        }


        public static Function1<Binding, Integer> pathSegments() {
            return new Function1<Binding, Integer>() {
                @Override
                public Integer call(Binding binding) throws Exception {
                    return binding.uriTemplate().segments();

                }
            };
        }

    }

}
