package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.ContractEnforcingResponse;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Callable;

class HttpExchangeResponse extends ContractEnforcingResponse {
    private final HttpExchange httpExchange;
    private boolean headersHaveBeenSent = false;

    public HttpExchangeResponse(final HttpExchange httpExchange) {
        super(responseBody(httpExchange));
        this.httpExchange = httpExchange;
    }

    private static Callable<OutputStream> responseBody(final HttpExchange httpExchange) {
        return new Callable<OutputStream>() {
            public OutputStream call() throws Exception {
                return httpExchange.getResponseBody();
            }
        };
    }

    @Override
    public Response status(Status value) {
        super.status(value);
        return this;
    }

    @Override
    public Response header(String name, String value) {
        httpExchange.getResponseHeaders().add(name, value);
        super.header(name, value);
        return this;
    }

    @Override
    public OutputStream output() {
        OutputStream outputStream = super.output();
        ensureHeadersHaveBeenSent();
        return outputStream;
    }

    private void ensureHeadersHaveBeenSent() {
        if(headersHaveBeenSent) return;
        try {
            httpExchange.sendResponseHeaders(status().code(), 0);
            headersHaveBeenSent=true;
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public void close() throws IOException {
        ensureHeadersHaveBeenSent();
        httpExchange.close();
        super.close();
    }
}
