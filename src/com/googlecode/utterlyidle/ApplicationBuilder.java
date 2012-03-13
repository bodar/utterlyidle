package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.utterlyidle.dsl.BindingBuilder;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.Modules;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Containers;
import com.googlecode.yadic.SimpleContainer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static com.googlecode.utterlyidle.dsl.DslBindings.binding;
import static com.googlecode.utterlyidle.dsl.DslBindings.bindings;
import static com.googlecode.utterlyidle.dsl.StaticBindingBuilder.in;
import static com.googlecode.utterlyidle.modules.Modules.applicationScopedClass;
import static com.googlecode.utterlyidle.modules.Modules.requestScopedClass;

public class ApplicationBuilder {
    private final Container container = new SimpleContainer();
    private List<Module> modules = new ArrayList<Module>();
    private List<Pair<? extends Predicate<? super Pair<Request, Response>>, ResponseHandler>> responseHandlers
            = new ArrayList<Pair<? extends Predicate<? super Pair<Request, Response>>, ResponseHandler>>();

    public static ApplicationBuilder application(Application application) {
        return new ApplicationBuilder(application);
    }

    public static ApplicationBuilder application(Class<? extends Application> application) {
        return new ApplicationBuilder(application);
    }

    public static ApplicationBuilder application() {
        return new ApplicationBuilder(RestApplication.class);
    }

    private ApplicationBuilder(Class<? extends Application> application) {
        container.add(Application.class, application);
        setupContainer();
    }

    private void setupContainer() {
        container.addInstance(ApplicationBuilder.class, this);
        Containers.decorateUsingActivator(container, Application.class, ActivateModules.class);
    }

    private ApplicationBuilder(Application application) {
        container.addInstance(Application.class, application);
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
        modules.add(module);
        return this;
    }

    public <T> ApplicationBuilder addResponseHandler(Predicate<? super Pair<Request, Response>> predicate, ResponseHandler responseHandler) {
        Pair<? extends Predicate<? super Pair<Request, Response>>, ResponseHandler> pair = pair(predicate, responseHandler);
        responseHandlers.add(pair);
        return this;
    }

    public Response handle(RequestBuilder request) throws Exception {
        return build().handle(request.build());
    }

    public String responseAsString(RequestBuilder request) throws Exception {
        Response response = handle(request);
        return Entity.asString(response);
    }

    private Application application;
    public Application build() {
        if(application == null ){
            container.addInstance(BasePath.class, basePath("/"));
            application = container.get(Application.class);
        }
        return application;
    }

    public Server start(ServerConfiguration configuration) {
        container.addInstance(ServerConfiguration.class, configuration);
        container.addInstance(BasePath.class, configuration.basePath());
        container.addActivator(Server.class, ServerActivator.class);
        return container.get(Server.class);
    }

    public Server start() {
        return start(defaultConfiguration());
    }

    public ApplicationBuilder addApplicationScopedClass(Class<?> aClass) {
        return add(applicationScopedClass(aClass));
    }

    public ApplicationBuilder addRequestScopedClass(Class<?> aClass) {
        return add(requestScopedClass(aClass));
    }

    public static class ActivateModules implements Callable<Application>{
        private final Application application;
        private final ApplicationBuilder builder;

        public ActivateModules(Application application, ApplicationBuilder builder) {
            this.application = application;
            this.builder = builder;
        }

        @Override
        public Application call() throws Exception {
            for (Module module : builder.modules) {
                application.add(module);
            }
            for (Pair<? extends Predicate<? super Pair<Request, Response>>, ResponseHandler> pair : builder.responseHandlers) {
                application.applicationScope().get(ResponseHandlers.class).add(pair.first(), pair.second());
            }
            return application;
        }
    }
}
