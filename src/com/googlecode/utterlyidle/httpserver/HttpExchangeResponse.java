package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.Response;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;

class HttpExchangeResponse extends Response {
    private final HttpExchange httpExchange;

    public HttpExchangeResponse(HttpExchange httpExchange) {
        super();
        output(new SendResponseHeadersOutputStream(this));
        this.httpExchange = httpExchange;
    }

    @Override
    public Response header(String name, String value) {
        httpExchange.getResponseHeaders().add(name, value);
        return super.header(name, value);
    }

    @Override
    public void close() throws IOException {
        super.close();
        httpExchange.close();
    }

    private static class SendResponseHeadersOutputStream extends ByteArrayOutputStream {
        private final HttpExchangeResponse response;

        public SendResponseHeadersOutputStream(HttpExchangeResponse response) {
            this.response = response;
        }

        @Override
        public void close() throws IOException {
            response.httpExchange.sendResponseHeaders(response.code().code(), 0);
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.httpExchange.getResponseBody()));
            writer.write(this.toString());
            writer.close();

            super.close();
        }
    }
}
