package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.Application;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class RestHandler implements HttpHandler {
    private final Application application;

    public RestHandler(Application application) {
        this.application = application;
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        HttpExchangeRequest request = new HttpExchangeRequest(httpExchange);
        HttpExchangeResponse response = new HttpExchangeResponse(httpExchange);
        try {
            application.handle(request, response);
            System.out.println(String.format("%s %s -> %s", request.method(), request.url(), response.code()));
        } catch (RuntimeException e) {
            System.err.println(String.format("%s %s -> %s", request.method(), request.url(), e.getCause()));
        }
    }
}
