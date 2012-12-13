package com.googlecode.utterlyidle.httpserver;

import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.ServerConfiguration;
import com.googlecode.utterlyidle.examples.HelloWorldApplication;
import com.googlecode.utterlyidle.modules.BasicAuditing;
import com.googlecode.utterlyidle.services.Service;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import static com.googlecode.totallylazy.callables.TimeCallable.calculateMilliseconds;
import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class RestServer implements Server {
    private HttpServer server;
    private Uri uri;

    public RestServer(final Application application, final ServerConfiguration configuration) throws Exception {
        server = startApp(application, configuration);
    }

    public void close() throws IOException {
        server.stop(0);
    }

    public static void main(String[] args) throws Exception {
        application(HelloWorldApplication.class).start(defaultConfiguration().port(8001));
    }

    public Uri uri() {
        return uri;
    }

    private HttpServer startApp(Application application, ServerConfiguration configuration) throws Exception {
        long start = nanoTime();
        HttpServer server = startUpServer(application, configuration);
        System.out.println(format("Listening on %s, started HttpServer in %s msecs", uri, calculateMilliseconds(start, nanoTime())));
        Service.functions.start().asyncApply(application);
        return server;
    }

    private HttpServer startUpServer(Application application, ServerConfiguration configuration) throws Exception {
        application.add(new BasicAuditing());
        HttpServer server = HttpServer.create(new InetSocketAddress(configuration.bindAddress(), configuration.port()), 0);
        server.createContext(configuration.basePath().toString(),
                new RestHandler(application));
        server.setExecutor(newFixedThreadPool(configuration.maxThreadNumber()));
        server.start();
        ServerConfiguration updatedConfiguration = configuration.port(server.getAddress().getPort());
        uri = updatedConfiguration.toUrl();
        return server;
    }
}
