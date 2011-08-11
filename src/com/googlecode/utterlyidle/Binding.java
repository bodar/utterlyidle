package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

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

    public Number numberOfArguments() {
        return parameters.size();
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

}
