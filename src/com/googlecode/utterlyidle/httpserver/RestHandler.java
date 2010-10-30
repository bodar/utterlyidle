package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.FormParameters;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.Parameters;
import com.googlecode.utterlyidle.QueryParameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
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
        System.out.println(String.format("%s %s", httpExchange.getRequestMethod(), httpExchange.getRequestURI()));
        try {
            application.handle(convertRequest(httpExchange), convertResponse(httpExchange) );
        } catch (RuntimeException e) {
            System.err.println(e.getCause());
        }
    }

    private Response convertResponse(final HttpExchange httpExchange) {
        return new HttpExchangeResponse(httpExchange);
    }

    private Request convertRequest(HttpExchange httpExchange) {
        HeaderParameters headers = addNameAndValue(headerParameters(), httpExchange.getRequestHeaders());
        QueryParameters query = parse(httpExchange.getRequestURI().getQuery());
        FormParameters form = parse(headers, httpExchange.getRequestBody());
        return request(httpExchange.getRequestMethod(), httpExchange.getRequestURI().getPath(), headers, query, form, httpExchange.getRequestBody());
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
