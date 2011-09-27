package com.googlecode.utterlyidle.examples;

import com.googlecode.utterlyidle.RestApplication;
import com.googlecode.utterlyidle.modules.Modules;

import java.util.Properties;

import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.get;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.queryParam;
import static com.googlecode.utterlyidle.dsl.DslBindings.binding;
import static com.googlecode.utterlyidle.modules.Modules.bindingsModule;
import static com.googlecode.utterlyidle.modules.Modules.requestInstance;

public class HelloWorldApplication extends RestApplication {
    public HelloWorldApplication() {
        add(bindingsModule(annotatedClass(HelloWorld.class)));
        add(bindingsModule(binding(get("/dsl").
                resource(method(on(Properties.class).getProperty(queryParam(String.class, "name"), queryParam(String.class, "default")))))));
        add(requestInstance(System.getProperties()));
    }
}
