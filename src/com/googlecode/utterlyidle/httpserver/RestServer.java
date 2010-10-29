package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.RestApplication;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import static com.googlecode.utterlyidle.BasePath.basePath;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class RestServer {
    private HttpServer server;

    public RestServer(int port, Application application) throws IOException {
        long start = nanoTime();
        server = HttpServer.create(new InetSocketAddress(port), 0);
        BasePath basePath = basePath("/");
        application.add(new RestModule(basePath));
        server.createContext(basePath.toString(), new RestHandler(application));
        server.setExecutor(newFixedThreadPool(50));
        server.start();
        System.out.println(format("Listening on %s, started in %s msecs", port, (nanoTime() - start) / 1000000.0));
    }

    public static void main(String[] args) throws IOException {
        new RestServer(8000, new RestApplication());
    }
}
