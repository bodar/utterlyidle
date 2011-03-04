package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.RestApplication;
import com.googlecode.utterlyidle.modules.RequestInstanceModule;
import com.googlecode.utterlyidle.modules.SingleResourceModule;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import static com.googlecode.totallylazy.callables.TimeCallable.calculateMilliseconds;
import static com.googlecode.utterlyidle.BasePath.basePath;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class RestServer {
    private HttpServer server;

    public RestServer(int port, BasePath basePath, Application application) throws IOException {
        long start = nanoTime();
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(basePath.toString(), new RestHandler(application.add(new RequestInstanceModule(basePath))));
        server.setExecutor(newFixedThreadPool(50));
        server.start();
        System.out.println(format("Listening on %s, started in %s msecs", port, calculateMilliseconds(start, nanoTime())));
    }

    public static void main(String[] args) throws IOException {
        new RestServer(8000, basePath("/"), new RestApplication().add(new SingleResourceModule(HelloWorld.class)));
    }
}
