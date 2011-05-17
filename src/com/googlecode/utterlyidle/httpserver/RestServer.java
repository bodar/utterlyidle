package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.RestApplication;
import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.ServerConfiguration;
import com.googlecode.utterlyidle.io.Url;
import com.googlecode.utterlyidle.jetty.RestApplicationActivator;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.RequestInstanceModule;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.utterlyidle.modules.SingleResourceModule;
import com.googlecode.yadic.Container;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

import static com.googlecode.totallylazy.callables.TimeCallable.calculateMilliseconds;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.get;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.queryParam;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class RestServer implements Server {
    private HttpServer server;
    private Url url;

    public RestServer(final Application application, final ServerConfiguration configuration) throws Exception {
        server = startApp(application, configuration);
    }

    public void close() throws IOException {
        server.stop(0);
    }

    public static void main(String[] args) throws Exception {
        new RestServer(new RestApplication(new SingleResourceModule(HelloWorld.class)), ServerConfiguration.defaultConfiguration().port(8000));
    }

    public Url getUrl() {
        return url;
    }

    private HttpServer startApp(Application application, ServerConfiguration configuration) throws Exception {
        long start = nanoTime();
        HttpServer server = startUpServer(application, configuration);
        System.out.println(format("Listening on %s, started HttpServer in %s msecs", url, calculateMilliseconds(start, nanoTime())));
        return server;
    }

    private HttpServer startUpServer(Application application, ServerConfiguration configuration) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(configuration.bindAddress(), configuration.serverUrl().port()), 0);
        server.createContext(configuration.serverUrl().path().toString(),
                new RestHandler(application.add(new RequestInstanceModule(configuration.serverUrl()))));
        server.setExecutor(newFixedThreadPool(configuration.maxThreadNumber()));
        server.start();
        updatePort(configuration, server);
        url = configuration.serverUrl();
        return server;
    }

    private void updatePort(ServerConfiguration configuration, HttpServer server) {
        configuration.port(server.getAddress().getPort());
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
