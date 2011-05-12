package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.io.Url;
import com.googlecode.utterlyidle.jetty.RestApplicationActivator;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.RequestInstanceModule;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.utterlyidle.modules.SingleResourceModule;
import com.googlecode.yadic.Container;
import com.sun.net.httpserver.HttpServer;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Properties;

import static com.googlecode.totallylazy.callables.TimeCallable.calculateMilliseconds;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.ServerConfiguration.serverConfiguration;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.get;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.queryParam;
import static com.googlecode.utterlyidle.io.Url.url;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class RestServer implements Server {
    private HttpServer server;
    private final Closeable closableActivator;
    private Url url;

    public RestServer(final CloseableCallable<Application> applicationActivator) throws Exception {
        this(applicationActivator, serverConfiguration());
    }

    public RestServer(final CloseableCallable<Application> applicationActivator, final ServerConfiguration configuration) throws Exception {
        closableActivator = applicationActivator;
        server = startApp(applicationActivator, configuration);
    }

    public void close() throws IOException {
        server.stop(0);
        closableActivator.close();
    }

    public static void main(String[] args) throws Exception {
        new RestServer(applicationActivator(), serverConfiguration().withPortNumber(8000));
    }

    public Url getUrl() {
        return url;
    }

    private HttpServer startApp(CloseableCallable<Application> applicationActivator, ServerConfiguration configuration) throws Exception {
        long start = nanoTime();
        HttpServer server = startUpServer(applicationActivator.call(), configuration);
        System.out.println(format("Listening on %s, started HttpServer in %s msecs", url, calculateMilliseconds(start, nanoTime())));
        return server;
    }

    private HttpServer startUpServer(Application application, ServerConfiguration configuration) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(configuration.bindAddress(), configuration.portNumber()), 0);
        server.createContext(configuration.basePath().toString(), new RestHandler(application.add(new RequestInstanceModule(configuration.basePath()))));
        server.setExecutor(newFixedThreadPool(configuration.maxThreadNumber()));
        server.start();
        url = url(format("http://localhost:%s%s", server.getAddress().getPort(), configuration.basePath()));
        return server;
    }

    private static RestApplicationActivator applicationActivator() {
        return new RestApplicationActivator(new SingleResourceModule(HelloWorld.class), new ResourcesModule() {
            public Module addResources(Resources resources) {
                resources.add(get("/dsl").resource(method(on(Properties.class).getProperty(queryParam(String.class, "name"), queryParam(String.class, "default")))));
                return this;
            }
        }, new RequestScopedModule() {
            public Module addPerRequestObjects(Container container) {
                container.addInstance(Properties.class, System.getProperties());
                return this;
            }
        });
    }
}
