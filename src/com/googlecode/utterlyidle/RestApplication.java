package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Block;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.bindings.BindingMatcher;
import com.googlecode.utterlyidle.bindings.DefaultBindingMatcher;
import com.googlecode.utterlyidle.bindings.actions.ResourceClass;
import com.googlecode.utterlyidle.handlers.AuditHandler;
import com.googlecode.utterlyidle.handlers.ContentLengthHandler;
import com.googlecode.utterlyidle.handlers.DateHandler;
import com.googlecode.utterlyidle.handlers.ExceptionHandler;
import com.googlecode.utterlyidle.handlers.HeadRequestHandler;
import com.googlecode.utterlyidle.handlers.ResponseHttpHandler;
import com.googlecode.utterlyidle.modules.CoreModule;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.Modules;
import com.googlecode.utterlyidle.rendering.exceptions.LastExceptionsHandler;
import com.googlecode.utterlyidle.rendering.exceptions.LastExceptionsModule;
import com.googlecode.utterlyidle.services.Services;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Containers;
import com.googlecode.yadic.SimpleContainer;
import com.googlecode.yadic.closeable.CloseableContainer;

import java.io.IOException;

import static com.googlecode.totallylazy.Callables.value;
import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.Binding.functions.action;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.bindings.actions.Action.functions.metaData;

public class RestApplication implements Application {
    private final ContainerFactory containerFactory;
    private final CloseableContainer applicationScope;
    private final Modules modules;
    private boolean closed = false;

    public RestApplication(BasePath basePath) {
        this(basePath, new UtterlyIdleProperties());
    }

    public RestApplication(BasePath basePath, UtterlyIdleProperties properties) {
        this(basePath, properties, new Module[0]);
    }

    public RestApplication(BasePath basePath, Module... modules) {
        this(basePath, new UtterlyIdleProperties(), modules);
    }

    public RestApplication(BasePath basePath, UtterlyIdleProperties properties, Module... modules) {
        this(basePath, properties, new DefaultContainerFactory(), modules);
    }

    public RestApplication(BasePath basePath, UtterlyIdleProperties properties, ContainerFactory containerFactory, Module... modules) {
        this.containerFactory = containerFactory;
        applicationScope = containerFactory.newCloseableContainer();
        applicationScope.addInstance(BasePath.class, basePath);
        applicationScope.addInstance(UtterlyIdleProperties.class, properties);
        applicationScope.addInstance(Application.class, this);
        applicationScope.removeCloseable(Application.class);
        this.modules = new Modules(properties);
        this.modules.setupApplicationScope(applicationScope);
        add(new CoreModule());
        add(new LastExceptionsModule());
        for (Module module : modules) {
            add(module);
        }
    }

    public Application add(final Module module) {
        checkNotClosed();
        modules.activateApplicationModule(module, applicationScope);
        usingRequestScope(activateStartupModule(module));
        return this;
    }

    private Block<Container> activateStartupModule(final Module module) {
        return new Block<Container>() {
            @Override
            protected void execute(Container container) throws Exception {
                container.addInstance(Request.class, get("/dummy/request/to/allow/starting/application").build());
                modules.activateStartupModule(module, container);
            }
        };
    }

    public Container applicationScope() {
        checkNotClosed();
        return applicationScope;
    }

    public Response handle(final Request request) throws Exception {
        checkNotClosed();
        return usingRequestScope(handleRequest(request));
    }

    public <T> T usingRequestScope(Callable1<Container, T> callable) {
        checkNotClosed();
        return using(requestScope(), callable);
    }

    protected CloseableContainer requestScope() {
        final CloseableContainer requestScope = containerFactory.newCloseableContainer(applicationScope);
        requestScope.add(BindingMatcher.class, DefaultBindingMatcher.class);
        requestScope.add(HttpHandler.class, BaseHandler.class);
        requestScope.decorate(HttpHandler.class, ResponseHttpHandler.class);
        requestScope.decorate(HttpHandler.class, DateHandler.class);
        modules.activateRequestModules(requestScope);
        requestScope.decorate(HttpHandler.class, BasePathHandler.class);
        requestScope.decorate(HttpHandler.class, LastExceptionsHandler.class);
        requestScope.decorate(HttpHandler.class, ExceptionHandler.class);
        requestScope.decorate(HttpHandler.class, ContentLengthHandler.class);
        requestScope.decorate(HttpHandler.class, HeadRequestHandler.class);
        requestScope.decorate(HttpHandler.class, AuditHandler.class);
        addResourcesIfNeeded(requestScope);
        return requestScope;
    }

    private void addResourcesIfNeeded(Container requestScope) {
        Bindings bindings = requestScope.get(Bindings.class);
        resourceClasses(bindings)
                .fold(requestScope, new Callable2<Container, Class, Container>() {
                    @Override
                    public Container call(Container container, Class aClass) throws Exception {
                        return Containers.addIfAbsent(container, aClass);
                    }
                });
    }

    private Sequence<Class> resourceClasses(Bindings bindings) {
        return sequence(bindings)
                .map(action())
                .flatMap(metaData(ResourceClass.class))
                .map(value(Class.class));
    }

    public <T> T usingArgumentScope(Request request, Callable1<Container, T> callable) {
        checkNotClosed();
        return using(argumentScope(request), callable);
    }

    protected Container argumentScope(Request request) {
        final Container argumentScope = new SimpleContainer();
        argumentScope.addInstance(Request.class, request);
        modules.activateArgumentModules(argumentScope);
        return argumentScope;
    }

    public static Callable1<Container, Response> handleRequest(final Request request) {
        return new Callable1<Container, Response>() {
            public Response call(Container container) throws Exception {
                return container.addInstance(Request.class, request).
                        get(HttpHandler.class).handle(request);
            }
        };
    }

    public static <T, R> Callable1<Container, R> inject(final T instance, final Callable1<Container, R> handler) {
        return new Callable1<Container, R>() {
            public R call(Container container) throws Exception {
                if (container.contains(instance.getClass())) {
                    container.remove(instance.getClass());
                }
                return handler.call(container.addInstance((Class) instance.getClass(), instance));
            }
        };
    }

    public void close() throws IOException {
        if(closed) return;
        applicationScope.close();
        closed = true;
    }

    @Override
    public void start() {
        checkNotClosed();
        applicationScope.get(Services.class).start();
    }

    @Override
    public void stop() {
        if(closed) return;
        applicationScope.get(Services.class).stop();
    }

    protected void checkNotClosed(){
        if(closed == true) throw new IllegalStateException("The application has been closed and can not be reused");
    }
}