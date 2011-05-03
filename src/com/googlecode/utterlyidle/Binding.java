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
    private final String consumes;
    private final String produces;
    private final Sequence<Pair<Type, Option<NamedParameter>>> arguments;
    private final int priority;
    private final boolean hidden;

    public Binding(Method method, UriTemplate uriTemplate, String httpMethod, String consumes, String produces, Sequence<Pair<Type, Option<NamedParameter>>> arguments, int priority, boolean hidden) {
        this.method = method;
        this.uriTemplate = uriTemplate;
        this.httpMethod = httpMethod;
        this.consumes = consumes;
        this.produces = produces;
        this.arguments = arguments;
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

    public String consumes() {
        return consumes;
    }

    public String produces() {
        return produces;
    }

    public Sequence<Pair<Type, Option<NamedParameter>>> parameters() {
        return arguments;
    }

    public Number numberOfArguments() {
        return arguments.size();
    }


    public int priority() {
        return priority;
    }

    public boolean hidden() {
        return hidden;
    }
}
