package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class Binding {
    private final Method method;
    private final String httpMethod;
    private final UriTemplate uriTemplate;
    private final Sequence<String> consumes;
    private final Sequence<String> produces;
    private final Sequence<Pair<Type, Option<NamedParameter>>> parameters;
    private final int priority;
    private final boolean hidden;

    public Binding(Method method, UriTemplate uriTemplate, String httpMethod, Sequence<String> consumes, Sequence<String> produces, Sequence<Pair<Type, Option<NamedParameter>>> parameters, int priority, boolean hidden) {
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

    public Sequence<Pair<Type, Option<NamedParameter>>> parameters() {
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
}
