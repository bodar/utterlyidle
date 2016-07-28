package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.utterlyidle.dsl.BindingBuilder;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.Modules;
import com.googlecode.utterlyidle.modules.ResponseHandlersModule;
import com.googlecode.utterlyidle.services.Service;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Containers;
import com.googlecode.yadic.SimpleContainer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.LazyException.lazyException;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Strings.EMPTY;
import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static com.googlecode.utterlyidle.dsl.DslBindings.binding;
import static com.googlecode.utterlyidle.dsl.DslBindings.bindings;
import static com.googlecode.utterlyidle.dsl.StaticBindingBuilder.in;
import static com.googlecode.utterlyidle.modules.Modules.applicationScopedClass;
import static com.googlecode.utterlyidle.modules.Modules.requestScopedClass;
import static com.googlecode.utterlyidle.modules.Modules.serviceClass;

public class ApplicationBuilder {
    private final Container container = new SimpleContainer();
    private List<Module> modules = new ArrayList<Module>();

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
        return addContent(baseUrl, path);
    }

    public ApplicationBuilder addContent(final URL baseUrl, final String path) {
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

    public <T> ApplicationBuilder addResponseHandler(final Predicate<? super Pair<Request, Response>> predicate, final ResponseHandler responseHandler) {
        return add(new ResponseHandlersModule() {
            @Override
            public ResponseHandlers addResponseHandlers(final ResponseHandlers handlers) throws Exception {
                return handlers.add(predicate, responseHandler);
            }
        });
    }

    @SuppressWarnings("deprecation")
    public Response handle(RequestBuilder request) throws Exception {
        return handle(request.build());
    }

    public Response handle(Request request) throws Exception {
        return build().handle(request);
    }

    @SuppressWarnings("deprecation")
    public String responseAsString(RequestBuilder request) throws Exception {
        return responseAsString(request.build());
    }

    public String responseAsString(Request build) throws Exception {
        Response response = handle(build);
        return response.entity().toString();
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

    public Server start(int port) {
        return start(defaultConfiguration().port(port));
    }

    public ApplicationBuilder addApplicationScopedClass(Class<?> aClass) {
        return add(applicationScopedClass(aClass));
    }

    public ApplicationBuilder addRequestScopedClass(Class<?> aClass) {
        return add(requestScopedClass(aClass));
    }

    public ApplicationBuilder addService(Class<? extends Service> aClass) {
        return add(serviceClass(aClass));
    }

    public static ApplicationBuilder staticApplication(File root) {
        return staticApplication(root, EMPTY);
    }

    public static ApplicationBuilder staticApplication(File root, String path) {
        try {
            return application().content(root.toURI().toURL(), path);
        } catch (MalformedURLException e) {
            throw lazyException(e);
        }
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
            return application;
        }
    }
}
