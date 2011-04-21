package com.googlecode.utterlyidle.dsl;

import com.googlecode.totallylazy.*;
import com.googlecode.totallylazy.proxy.CallOn;
import com.googlecode.totallylazy.proxy.Invocation;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.cookies.CookieParameters;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static com.googlecode.totallylazy.Sequences.sequence;

public class ActivatorBuilder {
    private Method method;
    private UriTemplate uriTemplate;
    private String httpMethod = "GET";
    private String consumes = "*/*";
    private String produces = "*/*";
    private int priority = 0;
    private List<Pair<Type,Option<NamedParameter>>> typesWithNamedParameter = Collections.emptyList();

    public Activator build(Application application) {
        return new HttpMethodActivator(method, uriTemplate, httpMethod, consumes, produces, new ParametersExtractor(uriTemplate, application, sequence(typesWithNamedParameter)), priority );
    }

    public ActivatorBuilder path(String value) {
        uriTemplate = UriTemplate.uriTemplate(value);
        return this;
    }

    public ActivatorBuilder method(Method method) {
        this.method = method;
        return this;
    }

    public ActivatorBuilder resource(Invocation invocation) {
        typesWithNamedParameter = parameters.get();
        parameters.remove();
        return method(invocation.method());
    }

    public ActivatorBuilder resource(CallOn callOn) {
        return resource(callOn.invocation());
    }

    public ActivatorBuilder httpMethod(String value){
        httpMethod = value;
        return this;
    }

    public static ActivatorBuilder get(String path) {
        return new ActivatorBuilder().path(path);
    }

    private static ThreadLocalParameters parameters = new ThreadLocalParameters();

    public static <T> T queryParam(Type type, String value) {
        Option<NamedParameter> some = Option.some(new NamedParameter(value, QueryParameters.class));
        parameters.get().add(Pair.pair(type, some));
        return null;
    }

    public static <T> T queryParam(Class<T> aClass, String value) {
        return ActivatorBuilder.<T>queryParam((Type)aClass, value);
    }

    public static <T> T formParam(Type type, String value) {
        Option<NamedParameter> some = Option.some(new NamedParameter(value, FormParameters.class));
        parameters.get().add(Pair.pair(type, some));
        return null;
    }

    public static <T> T formParam(Class<T> aClass, String value) {
        return ActivatorBuilder.<T>formParam((Type)aClass, value);
    }

    public static <T> T pathParam(Type type, String value) {
        Option<NamedParameter> some = Option.some(new NamedParameter(value, PathParameters.class));
        parameters.get().add(Pair.pair(type, some));
        return null;
    }

    public static <T> T pathParam(Class<T> aClass, String value) {
        return ActivatorBuilder.<T>pathParam((Type)aClass, value);
    }

    public static <T> T headerParam(Type type, String value) {
        Option<NamedParameter> some = Option.some(new NamedParameter(value, HeaderParameters.class));
        parameters.get().add(Pair.pair(type, some));
        return null;
    }

    public static <T> T headerParam(Class<T> aClass, String value) {
        return ActivatorBuilder.<T>headerParam((Type)aClass, value);
    }

    public static <T> T cookieParam(Type type, String value) {
        Option<NamedParameter> some = Option.some(new NamedParameter(value, CookieParameters.class));
        parameters.get().add(Pair.pair(type, some));
        return null;
    }

    public static <T> T cookieParam(Class<T> aClass, String value) {
        return ActivatorBuilder.<T>cookieParam((Type) aClass, value);
    }

}
