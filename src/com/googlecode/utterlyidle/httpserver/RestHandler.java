package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.Application;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

import static com.googlecode.totallylazy.callables.TimeCallable.calculateMilliseconds;
import static java.lang.System.nanoTime;

public class RestHandler implements HttpHandler {
    private final Application application;

    public RestHandler(Application application) {
        this.application = application;
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        HttpExchangeRequest request = new HttpExchangeRequest(httpExchange);
        HttpExchangeResponse response = new HttpExchangeResponse(httpExchange);
        try {
            long start = nanoTime();
            application.handle(request, response);
            System.out.println(String.format("%s %s -> %s in %s msecs", request.method(), request.url(), response.code(), calculateMilliseconds(start, nanoTime())));
        } catch (RuntimeException e) {
            System.err.println(String.format("%s %s -> %s", request.method(), request.url(), e.getCause()));
        }
    }
}
