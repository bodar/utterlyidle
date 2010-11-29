package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.Response;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

class HttpExchangeResponse extends Response {
    private final HttpExchange httpExchange;
    private boolean codeSent = false;

    public HttpExchangeResponse(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }

    @Override
    public Response header(String name, String value) {
        httpExchange.getResponseHeaders().add(name, value);
        return super.header(name, value);
    }

    @Override
    public OutputStream output() {
        sendCodeIfNeeded();
        return httpExchange.getResponseBody();
    }

    @Override
    public Response flush() throws IOException {
        super.flush();
        httpExchange.close();
        return this;
    }

    private void sendCodeIfNeeded() {
        try {
            if(!codeSent){
                httpExchange.sendResponseHeaders(code().code(), 0);
                codeSent = true;
            }
        } catch (Throwable e) {
            throw new UnsupportedOperationException(e);
        }

    }
}
