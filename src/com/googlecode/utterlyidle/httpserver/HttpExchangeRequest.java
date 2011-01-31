package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.MemoryRequest;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.Requests;
import com.googlecode.utterlyidle.io.Url;
import com.sun.net.httpserver.HttpExchange;

import java.util.List;
import java.util.Map;

import static com.googlecode.utterlyidle.Requests.*;

public class HttpExchangeRequest extends MemoryRequest {

    public HttpExchangeRequest(HttpExchange httpExchange, BasePath basePath) {
        super(httpExchange.getRequestMethod(), Url.url(httpExchange.getRequestURI().toString()),
                convert(httpExchange.getRequestHeaders()), getBytes(httpExchange.getRequestBody()), basePath);
    }

    private static HeaderParameters convert(Map<String, List<String>> requestHeaders) {
        HeaderParameters result = HeaderParameters.headerParameters();
        for (Map.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
            for (String value : entry.getValue()) {
                result.add(entry.getKey(), value);
            }
        }
        return result;
    }
}
