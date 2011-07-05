package com.googlecode.utterlyidle.examples;

import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.RestApplication;
import com.googlecode.utterlyidle.dsl.DslBindings;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.utterlyidle.modules.SingleResourceModule;
import com.googlecode.yadic.Container;

import java.util.Properties;

import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.get;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.queryParam;
import static com.googlecode.utterlyidle.dsl.DslBindings.binding;

public class HelloWorldApplication extends RestApplication {
    public HelloWorldApplication() {
        super(
                new SingleResourceModule(HelloWorld.class),
                new ResourcesModule() {
                    public Module addResources(Resources resources) {
                        resources.add(binding(get("/dsl").resource(method(on(Properties.class).getProperty(queryParam(String.class, "name"), queryParam(String.class, "default"))))));
                        return this;
                    }
                },
                new RequestScopedModule() {
                    public Module addPerRequestObjects(Container container) {
                        container.addInstance(Properties.class, System.getProperties());
                        return this;
                    }
                });
    }
}
