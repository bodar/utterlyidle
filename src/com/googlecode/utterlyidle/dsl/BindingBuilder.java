package com.googlecode.utterlyidle.dsl;

import com.googlecode.totallylazy.*;
import com.googlecode.totallylazy.annotations.multimethod;
import com.googlecode.totallylazy.proxy.CallOn;
import com.googlecode.totallylazy.proxy.Invocation;
import com.googlecode.totallylazy.proxy.MethodInvocation;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.annotations.HttpMethod;
import com.googlecode.utterlyidle.annotations.View;
import com.googlecode.utterlyidle.bindings.actions.Action;
import com.googlecode.utterlyidle.cookies.CookieParameters;
import com.googlecode.yadic.generics.TypeFor;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.MediaType.WILDCARD;
import static com.googlecode.utterlyidle.bindings.actions.InvokeResourceMethod.constructors.invokeResourceMethod;

public class BindingBuilder implements Callable<Binding> {
    private Action action;
    private UriTemplate uriTemplate;
    private String httpMethod = HttpMethod.GET;
    private Sequence<String> consumes = sequence(WILDCARD);
    private Sequence<String> produces = sequence(WILDCARD);
    private int priority = 0;
    private Sequence<Pair<Type,Option<Parameter>>> typesWithParameter = Sequences.empty();
    private boolean hidden = false;
    private View view;


    public static BindingBuilder modify(Binding binding) {
        return new BindingBuilder().
                action(binding.action()).
                uriTemplate(binding.uriTemplate()).
                httpMethod(binding.httpMethod()).
                consumes(binding.consumes()).
                produces(binding.produces()).
                priority(binding.priority()).
                parameters(binding.parameters()).
                hidden(binding.hidden()).
                view(binding.view());
    }

    public Binding call() throws Exception {
        return build();
    }

    public Binding build() {
        return new Binding(action, uriTemplate, httpMethod, consumes, produces, sequence(typesWithParameter), priority, hidden, view);
    }

    public BindingBuilder path(String value) {
        return uriTemplate(UriTemplate.uriTemplate(value));
    }

    public BindingBuilder uriTemplate(UriTemplate value) {
        uriTemplate = value;
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

    public BindingBuilder action(Action value) {
        action = value;
        return this;
    }

    public BindingBuilder method(Method value) {
        return action(invokeResourceMethod(value));
    }

    private multi multi;
    public BindingBuilder resource(final Invocation invocation) {
        if(multi == null) multi = new multi(){};
        return multi.method(invocation);
    }

    @multimethod
    public BindingBuilder resource(final MethodInvocation invocation) {
        if(view == null) view = View.constructors.view(invocation.method().getName());
        parameters(parameters.get());
        parameters.remove();
        return action(invokeResourceMethod(invocation.method()));
    }

    public BindingBuilder parameters(Iterable<Pair<Type, Option<Parameter>>> value) {
        typesWithParameter = sequence(value);
        return this;
    }


    public BindingBuilder resource(CallOn callOn) {
        return resource(callOn.invocation());
    }

    public BindingBuilder httpMethod(String value){
        httpMethod = value;
        return this;
    }

    public BindingBuilder view(final String value) {
        view  = View.constructors.view(value);
        return this;
    }

    private BindingBuilder view(final View some) {
        return view(some.value());
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

    public static BindingBuilder patch(String path) {
        return new BindingBuilder().httpMethod(HttpMethod.PATCH).path(path);
    }

    private static ThreadLocalParameters parameters = new ThreadLocalParameters();

    private static <T> T namedParameter(Type type, String name, final Class<? extends Parameters<?>> parametersClass, final Option<String> defaultValue) {
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

    public static InputStream entity() {
        return param(InputStream.class);
    }

    public static <T> T param(Class<T> type) {
        return param((Type)type);
    }

    public static <T> T param(TypeFor<T> typeFor) {
        return param(typeFor.get());
    }

    public static <T> T param(Type type) {
        parameters.get().add(Pair.<Type, Option<Parameter>>pair(type, none(Parameter.class)));
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
