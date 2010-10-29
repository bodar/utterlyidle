package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.FormParameters;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.Parameters;
import com.googlecode.utterlyidle.QueryParameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import static com.googlecode.utterlyidle.FormParameters.formParameters;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.QueryParameters.queryParameters;
import static com.googlecode.utterlyidle.Request.request;

public class RestHandler implements HttpHandler {
    private final Application application;

    public RestHandler(Application application) {
        this.application = application;
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("httpExchange.getRequestURI() = " + httpExchange.getRequestURI());
        application.handle(convertRequest(httpExchange), convertResponse(httpExchange) );
    }

    private Response convertResponse(final HttpExchange httpExchange) {
        return new Response(){
            @Override
            public Response code(Status value) {
                try {
                    httpExchange.sendResponseHeaders(value.code(), 0);
                    return this;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public OutputStream output() {
                return httpExchange.getResponseBody();
            }
        };
    }

    private Request convertRequest(HttpExchange httpExchange) {
        HeaderParameters headers = addNameAndValue(headerParameters(), httpExchange.getRequestHeaders());
        QueryParameters query = parse(httpExchange.getRequestURI().getQuery());
        FormParameters form = parse(headers, httpExchange.getRequestBody());
        return request(httpExchange.getRequestMethod(), httpExchange.getRequestURI().toString(), headers, query, form, httpExchange.getRequestBody());
    }

    private FormParameters parse(HeaderParameters headers, InputStream requestBody) {
        return formParameters();
    }

    private QueryParameters parse(String query) {
        return queryParameters();
    }

    private <T extends Parameters> T addNameAndValue(T parameters, Map<String,List<String>> requestHeaders) {
        for (Map.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
            for (String value : entry.getValue()) {
                parameters.add(entry.getKey(), value);
            }
        }
        return parameters;
    }
}
