package com.googlecode.utterlyidle.httpserver;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.MemoryRequest;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Requests;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Runnables.write;
import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.utterlyidle.ClientAddress.clientAddress;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.HeaderParameters.withXForwardedFor;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_LENGTH;
import static com.googlecode.utterlyidle.Responses.response;

public class RestHandler implements HttpHandler {
    private final Application application;

    public RestHandler(Application application) {
        this.application = application;
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        Response response = handle(request(httpExchange));
        mapTo(response, httpExchange);
    }

    private Response handle(Request request) throws IOException {
        try {
            return application.handle(request);
        } catch (Exception e) {
            return exceptionResponse(request, e);
        }
    }

    private void mapTo(Response response, HttpExchange httpExchange) throws IOException {
        for (Pair<String, String> pair : response.headers()) {
            httpExchange.getResponseHeaders().add(pair.first(), pair.second());
        }
        long length = Long.parseLong(response.header(CONTENT_LENGTH));
        httpExchange.sendResponseHeaders(response.status().code(), length == 0 ? -1 : length);
        using(httpExchange.getResponseBody(), write(response.bytes()));
        httpExchange.close();
    }


    private MemoryRequest request(HttpExchange httpExchange) {
        return Requests.request(
                httpExchange.getRequestMethod(),
                uri(httpExchange.getRequestURI().toString()),
                withXForwardedFor(clientAddress(httpExchange.getRemoteAddress().getAddress()), headerParameters(httpExchange.getRequestHeaders())),
                bytes(httpExchange.getRequestBody())
        );
    }

    private Response exceptionResponse(Request request, final Exception e) throws IOException {
        System.err.println(String.format("%s %s -> %s", request.method(), request.uri(), e));
        e.printStackTrace(System.err);
        Response response = response().status(Status.INTERNAL_SERVER_ERROR);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(stream));
        return response.bytes(stream.toByteArray());
    }

}
