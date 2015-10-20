package com.googlecode.utterlyidle.httpserver;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Value;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.HttpHeaders;
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
import static com.googlecode.totallylazy.io.Uri.uri;
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

        httpExchange.sendResponseHeaders(response.status().code(), ContentLength.handle(response).value());
        using(httpExchange.getResponseBody(), response.entity().writer());
        httpExchange.close();
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
                extractScheme(httpExchange.getProtocol()))
                .enrich(request);
    }

    private String extractScheme(final String protocol) {
        return protocol.toLowerCase().split("/")[0];
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

class ContentLength {
    static ResponseType responseType(final long value){
        return () -> value;
    }

    static ResponseType NoContent = responseType(-1); // Yes "no content" is NOT 0!
    static ResponseType Streaming = responseType(0); // Yes streaming is 0!
    static ResponseType Content(long value) {
        if(value == 0) return NoContent;
        return responseType(value);
    }

    interface ResponseType extends Value<Long> { }

    static ResponseType handle(final Response response) {
        if(response.entity().isStreaming()) return Streaming;
        for (String length : response.headers().valueOption(HttpHeaders.CONTENT_LENGTH)) return Content(Long.parseLong(length));
        return NoContent;
    }

}