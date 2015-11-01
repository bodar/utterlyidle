package com.googlecode.utterlyidle.undertow;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.functions.Function2;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.Entity;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.QueryParameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Exceptions.printStackTrace;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.ClientAddress.clientAddress;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.MediaType.TEXT_PLAIN;
import static com.googlecode.utterlyidle.RequestEnricher.requestEnricher;
import static com.googlecode.utterlyidle.Status.INTERNAL_SERVER_ERROR;
import static java.lang.Integer.parseInt;

class RestHttpHandler implements HttpHandler {
    private final Application application;

    public RestHttpHandler(Application application) {
        this.application = application;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws IOException {
        exchange.startBlocking();
        Response applicationResponse = getResponse(exchange);
        mapTo(applicationResponse, exchange);
    }

    private Response getResponse(HttpServerExchange exchange) {
        try {
            return application.handle(request(exchange));

        } catch (Throwable e) {
            StringWriter stringWriter = new StringWriter();
            using(new PrintWriter(stringWriter), printStackTrace(e));
            return ResponseBuilder.response(INTERNAL_SERVER_ERROR).
                    contentType(TEXT_PLAIN).
                    entity(stringWriter.toString()).
                    build();
        }
    }

    private void mapTo(Response applicationResponse, HttpServerExchange exchange) throws IOException {
        exchange.setResponseCode(applicationResponse.status().code());
        sequence(applicationResponse.headers()).fold(exchange, mapHeaders());
        for (String integer : applicationResponse.headers().valueOption(HttpHeaders.CONTENT_LENGTH)) {
            exchange.setResponseContentLength(parseInt(integer));
        }
        using(exchange.getOutputStream(), applicationResponse.entity().writer());
    }

    private Function2<HttpServerExchange, Pair<String, String>, HttpServerExchange> mapHeaders() {
        return (exchange, applicationHeader) -> {
            exchange.getResponseHeaders().add(new HttpString(applicationHeader.first()), applicationHeader.second());
            return exchange;
        };
    }

    private Request request(HttpServerExchange exchange) throws IOException {
        Request request = Request.request(exchange.getRequestMethod().toString(), Uri.uri(exchange.getRequestPath()).query(query(exchange).toString()), headers(exchange), Entity.entity(exchange.getInputStream()));
        return requestEnricher(
                clientAddress(exchange.getSourceAddress().getAddress()),
                exchange.getRequestScheme())
                .enrich(request);
    }

    private HeaderParameters headers(HttpServerExchange exchange) {
        return headerParameters(sequence(exchange.getRequestHeaders())
                .flatMap(headerValues -> sequence(headerValues)
                        .map(headerValue -> pair(headerValues.getHeaderName().toString(), headerValue))));
    }

    private QueryParameters query(HttpServerExchange exchange) {
        return QueryParameters.parse(exchange.getQueryString());
    }
}
