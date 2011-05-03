package com.googlecode.utterlyidle.dsl;

import com.googlecode.totallylazy.*;
import com.googlecode.totallylazy.proxy.CallOn;
import com.googlecode.totallylazy.proxy.Invocation;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.cookies.CookieParameters;

import javax.ws.rs.HttpMethod;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static com.googlecode.totallylazy.Sequences.sequence;

public class BindingBuilder {
    private Method method;
    private UriTemplate uriTemplate;
    private String httpMethod = HttpMethod.GET;
    private String consumes = "*/*";
    private String produces = "*/*";
    private int priority = 0;
    private List<Pair<Type,Option<NamedParameter>>> typesWithNamedParameter = Collections.emptyList();
    private boolean hidden = false;

    public Binding build() {
        return new Binding(method, uriTemplate, httpMethod, consumes, produces, sequence(typesWithNamedParameter), priority, hidden);
    }

    public BindingBuilder path(String value) {
        uriTemplate = UriTemplate.uriTemplate(value);
        return this;
    }

    public BindingBuilder hidden(boolean value){
        hidden = value;
        return this;
    }

    public BindingBuilder consumes(String value) {
        consumes = value;
        return this;
    }

    public BindingBuilder produces(String value) {
        produces = value;
        return this;
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
        typesWithNamedParameter = parameters.get();
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

    public static <T> T queryParam(Type type, String value) {
        Option<NamedParameter> some = Option.some(new NamedParameter(value, QueryParameters.class));
        parameters.get().add(Pair.pair(type, some));
        return null;
    }

    public static <T> T queryParam(Class<T> aClass, String value) {
        return BindingBuilder.<T>queryParam((Type) aClass, value);
    }

    public static <T> T formParam(Type type, String value) {
        Option<NamedParameter> some = Option.some(new NamedParameter(value, FormParameters.class));
        parameters.get().add(Pair.pair(type, some));
        return null;
    }

    public static <T> T formParam(Class<T> aClass, String value) {
        return BindingBuilder.<T>formParam((Type) aClass, value);
    }

    public static <T> T pathParam(Type type, String value) {
        Option<NamedParameter> some = Option.some(new NamedParameter(value, PathParameters.class));
        parameters.get().add(Pair.pair(type, some));
        return null;
    }

    public static <T> T pathParam(Class<T> aClass, String value) {
        return BindingBuilder.<T>pathParam((Type) aClass, value);
    }

    public static <T> T headerParam(Type type, String value) {
        Option<NamedParameter> some = Option.some(new NamedParameter(value, HeaderParameters.class));
        parameters.get().add(Pair.pair(type, some));
        return null;
    }

    public static <T> T headerParam(Class<T> aClass, String value) {
        return BindingBuilder.<T>headerParam((Type) aClass, value);
    }

    public static <T> T cookieParam(Type type, String value) {
        Option<NamedParameter> some = Option.some(new NamedParameter(value, CookieParameters.class));
        parameters.get().add(Pair.pair(type, some));
        return null;
    }

    public static <T> T cookieParam(Class<T> aClass, String value) {
        return BindingBuilder.<T>cookieParam((Type) aClass, value);
    }

}
