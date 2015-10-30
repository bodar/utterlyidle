package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.functions.Callables;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.predicates.LogicalPredicate;
import com.googlecode.utterlyidle.annotations.View;
import com.googlecode.utterlyidle.bindings.actions.Action;
import com.googlecode.yadic.Container;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static com.googlecode.totallylazy.Option.identity;
import static com.googlecode.totallylazy.predicates.Predicates.where;
import static com.googlecode.utterlyidle.NamedParameter.methods.defaultValue;

public class Binding  {
    private final Action action;
    private final String httpMethod;
    private final UriTemplate uriTemplate;
    private final Sequence<String> consumes;
    private final Sequence<String> produces;
    private final Sequence<Pair<Type, Option<Parameter>>> parameters;
    private final int priority;
    private final boolean hidden;
    private final View view;

    public Binding(Action action,
                   UriTemplate uriTemplate,
                   String httpMethod,
                   Sequence<String> consumes,
                   Sequence<String> produces,
                   Sequence<Pair<Type, Option<Parameter>>> parameters,
                   int priority,
                   boolean hidden,
                   final View view) {
        this.action = action;
        this.uriTemplate = uriTemplate;
        this.httpMethod = httpMethod;
        this.consumes = consumes;
        this.produces = produces;
        this.view = view;
        this.parameters = parameters.realise();
        this.priority = priority;
        this.hidden = hidden;
    }

    public Object invoke(Container container) throws Exception {
        return action().invoke(container);
    }
    public Action action() {
        return action;
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

    public Sequence<NamedParameter> namedParameters() {
        return parameters.map(Callables.<Option<Parameter>>second()).
                flatMap(identity(Parameter.class)).
                safeCast(NamedParameter.class);
    }

    public int numberOfArguments() {
        return parameters.size();
    }

    private int numberOfDefaultArguments() {
        return namedParameters().flatMap(defaultValue()).size();
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

    protected Sequence<Object> myFields() {
        return Sequences.sequence(action, httpMethod, uriTemplate, consumes, produces, parameters, priority);
    }

    @Override
    public String toString() {
        return String.format("%s %s -> %s", httpMethod, uriTemplate, action.description());
    }

    public View view() {
        return view;
    }

    public static class functions {

        public static LogicalPredicate<Binding> isForMethod(final Method method) {
            return where(action(), Action.functions.isForMethod(method));
        }
        public static Function1<Binding, Action> action() {
            return binding -> binding.action();
        }

        public static Function1<Binding, Integer> priority() {
            return binding -> binding.priority();
        }

        public static Function1<Binding, Integer> numberOfArguments() {
            return binding -> binding.numberOfArguments();
        }

        public static Function1<Binding, Integer> numberOfDefaultArguments() {
            return binding -> binding.numberOfDefaultArguments();
        }


        public static Function1<Binding, Integer> pathSegments() {
            return binding -> binding.uriTemplate().segments();
        }

    }

}
