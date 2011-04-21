package com.googlecode.utterlyidle.dsl;

import com.googlecode.totallylazy.*;
import com.googlecode.totallylazy.proxy.CallOn;
import com.googlecode.totallylazy.proxy.Invocation;
import com.googlecode.utterlyidle.*;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class ActivatorBuilder {
    private Method method;
    private UriTemplate uriTemplate;
    private String httpMethod = "GET";
    private String consumes = "*/*";
    private String produces = "*/*";
    private int priority = 0;

    public Activator build(Application application) {
        return new HttpMethodActivator(method, uriTemplate, httpMethod, consumes, produces, new ParametersExtractor(uriTemplate, application, Sequences.<Pair<Type,Option<NamedParameter>>>empty()), priority );
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
}
