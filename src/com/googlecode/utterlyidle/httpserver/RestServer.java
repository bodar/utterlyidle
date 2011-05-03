package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.CloseableCallable;
import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.io.Url;
import com.googlecode.utterlyidle.jetty.RestApplicationActivator;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.RequestInstanceModule;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.utterlyidle.modules.SingleResourceModule;
import com.sun.net.httpserver.HttpServer;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

import static com.googlecode.totallylazy.callables.TimeCallable.calculateMilliseconds;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.dsl.ActivatorBuilder.get;
import static com.googlecode.utterlyidle.dsl.ActivatorBuilder.queryParam;
import static com.googlecode.utterlyidle.io.Url.url;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class RestServer implements Server {
    private HttpServer server;
    private final Closeable closableActivator;
    private Url url;

    public RestServer(int port, BasePath basePath, CloseableCallable<Application> applicationActivator) throws Exception {
        this.closableActivator = applicationActivator;
        server = startApp(port, basePath, applicationActivator.call());
    }

    public RestServer(BasePath basePath, CloseableCallable<Application> applicationActivator) throws Exception {
        this(0, basePath, applicationActivator);
    }

    public void close() throws IOException {
        server.stop(0);
        closableActivator.close();
    }

    public static void main(String[] args) throws Exception {
        new RestServer(8000, basePath("/"), new RestApplicationActivator(new SingleResourceModule(HelloWorld.class), new ResourcesModule() {
            public Module addResources(Resources resources) {
                resources.add(get("/dsl").resource(method(on(Properties.class).getProperty(queryParam(String.class, "name"), queryParam(String.class, "default")))));
                return this;
            }
        }));
    }

    private HttpServer startApp(int port, BasePath basePath, Application application) throws Exception{
        long start = nanoTime();
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(basePath.toString(), new RestHandler(application.add(new RequestInstanceModule(basePath))));
        server.setExecutor(newFixedThreadPool(50));
        server.start();
        url = url(format("http://localhost:%s%s", server.getAddress().getPort(), basePath));
        System.out.println(format("Listening on %s, started HttpServer in %s msecs", url, calculateMilliseconds(start, nanoTime())));
        return server;
    }

    public Url getUrl() {
        return url;
    }
}
