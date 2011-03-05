package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.RestApplication;
import com.googlecode.utterlyidle.Server;
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

public class RestServer implements Server {
    private HttpServer server;

    public RestServer(int port, BasePath basePath, Application application) throws Exception {
        long start = nanoTime();
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(basePath.toString(), new RestHandler(application.add(new RequestInstanceModule(basePath))));
        server.setExecutor(newFixedThreadPool(50));
        start();
        System.out.println(format("Listening on %s, started in %s msecs", port, calculateMilliseconds(start, nanoTime())));
    }

    public Server start() throws Exception{
        server.start();
        return this;
    }

    public Server stop() throws Exception{
        server.stop(0);
        return this;
    }

    public static void main(String[] args) throws Exception {
        new RestServer(8000, basePath("/"), new RestApplication().add(new SingleResourceModule(HelloWorld.class)));
    }
}
