package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callers;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Strings;
import com.googlecode.utterlyidle.dsl.BindingBuilder;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.io.Url;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.Modules;

import java.net.URL;

import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static com.googlecode.utterlyidle.dsl.DslBindings.binding;
import static com.googlecode.utterlyidle.dsl.DslBindings.bindings;
import static com.googlecode.utterlyidle.dsl.StaticBindingBuilder.in;
import static com.googlecode.utterlyidle.modules.Modules.applicationScopedClass;
import static com.googlecode.utterlyidle.modules.Modules.requestScopedClass;

public class ApplicationBuilder {
    private final Application application;

    public static ApplicationBuilder application(Application application) {
        return new ApplicationBuilder(application);
    }

    public static ApplicationBuilder application() {
        return application(new RestApplication());
    }

    private ApplicationBuilder(Application application) {
        this.application = application;
    }

    public ApplicationBuilder content(final URL baseUrl, final String path) {
        return add(bindings(in(baseUrl).path(path)));
    }

    public ApplicationBuilder addAnnotated(Class<?> resource) {
        return add(annotatedClass(resource));
    }

    public ApplicationBuilder add(BindingBuilder builder) {
        return add(binding(builder));
    }

    public ApplicationBuilder add(final Binding... bindings) {
        return add(Modules.bindingsModule(bindings));
    }

    public ApplicationBuilder add(Module module) {
        application.add(module);
        return this;
    }

    public <T> ApplicationBuilder addResponseHandler(Predicate<? super Pair<Request, Response>> predicate, ResponseHandler responseHandler) {
        application.applicationScope().get(ResponseHandlers.class).add(predicate, responseHandler);
        return this;
    }

    public Response handle(RequestBuilder request) throws Exception {
        return build().handle(request.build());
    }

    public String responseAsString(RequestBuilder request) throws Exception {
        return Strings.toString(handle(request).bytes());
    }

    public Application build() {
        return application;
    }

    public Url start(ServerConfiguration configuration) {
        return Callers.call(new ServerActivator(build(), configuration)).getUrl();
    }

    public Url start() {
        return start(defaultConfiguration());
    }

    public ApplicationBuilder addApplicationScopedClass(Class<?> aClass) {
        return add(applicationScopedClass(aClass));
    }

    public ApplicationBuilder addRequestScopedClass(Class<?> aClass) {
        return add(requestScopedClass(aClass));
    }
}
