package com.googlecode.utterlyidle.httpserver;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Requests;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.Status;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.utterlyidle.ClientAddress.clientAddress;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.RequestEnricher.requestEnricher;
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

        httpExchange.sendResponseHeaders(response.status().code(), fixBadApiDesign(response.entity().length()));
        using(httpExchange.getResponseBody(), response.entity().writer());
        httpExchange.close();
    }

    private long fixBadApiDesign(final Option<Integer> length) {
        if (length.isEmpty()) return 0;
        if (length.get() == 0) return -1;
        return length.get();
    }

    private Request request(HttpExchange httpExchange) {
        Request request = Requests.request(
                httpExchange.getRequestMethod(),
                uri(httpExchange.getRequestURI().toString()),
                headerParameters(httpExchange.getRequestHeaders()),
                httpExchange.getRequestBody()
        );
        return requestEnricher(
                clientAddress(httpExchange.getRemoteAddress().getAddress()),
                httpExchange.getProtocol())
                .enrich(request);
    }

    private Response exceptionResponse(Request request, final Exception e) throws IOException {
        System.err.println(String.format("%s %s -> %s", request.method(), request.uri(), e));
        e.printStackTrace(System.err);
        Response response = response(Status.INTERNAL_SERVER_ERROR);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(stream));
        return ResponseBuilder.modify(response).entity(stream.toByteArray()).build();
    }

}
