package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.io.Url;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
        return request(httpExchange.getRequestMethod(), Url.url(httpExchange.getRequestURI().toString()),
                convertToHeaderParameters(httpExchange.getRequestHeaders()), httpExchange.getRequestBody());
    }

    private HeaderParameters convertToHeaderParameters(Map<String,List<String>> requestHeaders) {
        HeaderParameters result  = HeaderParameters.headerParameters();
        for (Map.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
            for (String value : entry.getValue()) {
                result.add(entry.getKey(), value);
            }
        }
        return result;
    }

}
