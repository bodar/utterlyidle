package com.googlecode.utterlyidle.dsl;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.proxy.CallOn;
import com.googlecode.totallylazy.proxy.Invocation;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.annotations.HttpMethod;
import com.googlecode.utterlyidle.cookies.CookieParameters;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.MediaType.WILDCARD;

public class BindingBuilder implements Callable<Binding> {
    private Method method;
    private UriTemplate uriTemplate;
    private String httpMethod = HttpMethod.GET;
    private Sequence<String> consumes = sequence(WILDCARD);
    private Sequence<String> produces = sequence(WILDCARD);
    private int priority = 0;
    private List<Pair<Type,Option<Parameter>>> typesWithParameter = Collections.emptyList();
    private boolean hidden = false;

    public Binding call() throws Exception {
        return build();
    }

    public Binding build() {
        return new Binding(method, uriTemplate, httpMethod, consumes, produces, sequence(typesWithParameter), priority, hidden);
    }

    public BindingBuilder path(String value) {
        uriTemplate = UriTemplate.uriTemplate(value);
        return this;
    }

    public BindingBuilder hidden(boolean value){
        hidden = value;
        return this;
    }

    public BindingBuilder consumes(Iterable<String> values) {
        consumes = sequence(values);
        return this;
    }

    public BindingBuilder consumes(String... values) {
        return consumes(sequence(values));
    }

    public BindingBuilder produces(Iterable<String> values) {
        produces = sequence(values);
        return this;
    }

    public BindingBuilder produces(String... values) {
        return produces(sequence(values));
    }

    public BindingBuilder priority(int value) {
        priority = value;
        return this;
    }

    public BindingBuilder method(Method value) {
        method = value;
        return this;
    }

    public BindingBuilder resource(Invocation invocation) {
        typesWithParameter = parameters.get();
        parameters.remove();
        return method(invocation.method());
    }

    public BindingBuilder resource(CallOn callOn) {
        return resource(callOn.invocation());
    }

    public BindingBuilder httpMethod(String value){
        httpMethod = value;
        return this;
    }

    public static BindingBuilder get(String path) {
        return new BindingBuilder().path(path);
    }

    public static BindingBuilder post(String path) {
        return new BindingBuilder().httpMethod(HttpMethod.POST).path(path);
    }

    public static BindingBuilder delete(String path) {
        return new BindingBuilder().httpMethod(HttpMethod.DELETE).path(path);
    }

    public static BindingBuilder put(String path) {
        return new BindingBuilder().httpMethod(HttpMethod.PUT).path(path);
    }

    private static ThreadLocalParameters parameters = new ThreadLocalParameters();

    private static <T> T namedParameter(Type type, String name, final Class<? extends Parameters<String, String, ?>> parametersClass, final Option<String> defaultValue) {
        Option<Parameter> namedParameterSome = Option.<Parameter>some(new NamedParameter(name, parametersClass, defaultValue));
        parameters.get().add(Pair.pair(type, namedParameterSome));
        return null;
    }

    public static <T> T definedParam(final T value) {
        return definedParam(value.getClass(), value);
    }

    public static <T> T definedParam(final Type type, final T value) {
        Option<Parameter> some = Option.<Parameter>some(new DefinedParameter<T>(type, value));
        Pair<Type,Option<Parameter>> pair = Pair.pair(type, some);
        parameters.get().add(pair);
        return null;
    }

    public static <T> T queryParam(Type type, String name) {
        return BindingBuilder.<T>namedParameter(type, name, QueryParameters.class, Option.<String>none());
    }

    public static <T> T queryParam(Type type, String name, String defaultValue) {
        return BindingBuilder.<T>namedParameter(type, name, QueryParameters.class, some(defaultValue));
    }

    public static <T> T queryParam(Class<T> aClass, String name) {
        return BindingBuilder.<T>namedParameter((Type) aClass, name, QueryParameters.class, Option.<String>none());
    }

    public static <T> T queryParam(Class<T> aClass, String name, String defaultValue) {
        return BindingBuilder.<T>namedParameter(aClass, name, QueryParameters.class, some(defaultValue));
    }

    public static <T> T formParam(Type type, String name) {
        return BindingBuilder.<T>namedParameter(type, name, FormParameters.class, Option.<String>none());
    }

    public static <T> T formParam(Type type, String name, String defaultValue) {
        return BindingBuilder.<T>namedParameter(type, name, FormParameters.class, some(defaultValue));
    }

    public static <T> T formParam(Class<T> aClass, String name) {
        return BindingBuilder.<T>namedParameter((Type) aClass, name, FormParameters.class, Option.<String>none());
    }

    public static <T> T formParam(Class<T> aClass, String name, String defaultValue) {
        return BindingBuilder.<T>namedParameter(aClass, name, FormParameters.class, some(defaultValue));
    }

    public static <T> T pathParam(Type type, String name) {
        return BindingBuilder.<T>namedParameter(type, name, PathParameters.class, Option.<String>none());
    }

    public static <T> T pathParam(Type type, String name, String defaultValue) {
        return BindingBuilder.<T>namedParameter(type, name, PathParameters.class, some(defaultValue));
    }

    public static <T> T pathParam(Class<T> aClass, String name) {
        return BindingBuilder.<T>namedParameter((Type) aClass, name, PathParameters.class, Option.<String>none());
    }

    public static <T> T pathParam(Class<T> aClass, String name, String defaultValue) {
        return BindingBuilder.<T>namedParameter(aClass, name, PathParameters.class, some(defaultValue));
    }

    public static <T> T headerParam(Type type, String name) {
        return BindingBuilder.<T>namedParameter(type, name, HeaderParameters.class, Option.<String>none());
    }

    public static <T> T headerParam(Type type, String name, String defaultValue) {
        return BindingBuilder.<T>namedParameter(type, name, HeaderParameters.class, some(defaultValue));
    }

    public static <T> T headerParam(Class<T> aClass, String name) {
        return BindingBuilder.<T>namedParameter((Type) aClass, name, HeaderParameters.class, Option.<String>none());
    }

    public static <T> T headerParam(Class<T> aClass, String name, String defaultValue) {
        return BindingBuilder.<T>namedParameter(aClass, name, HeaderParameters.class, some(defaultValue));
    }

    public static <T> T cookieParam(Type type, String name) {
        return BindingBuilder.<T>namedParameter(type, name, CookieParameters.class, Option.<String>none());
    }

    public static <T> T cookieParam(Type type, String name, String defaultValue) {
        return BindingBuilder.<T>namedParameter(type, name, CookieParameters.class, some(defaultValue));
    }

    public static <T> T cookieParam(Class<T> aClass, String name) {
        return BindingBuilder.<T>namedParameter((Type) aClass, name, CookieParameters.class, Option.<String>none());
    }

    public static <T> T cookieParam(Class<T> aClass, String name, String defaultValue) {
        return BindingBuilder.<T>namedParameter(aClass, name, CookieParameters.class, some(defaultValue));
    }
}
