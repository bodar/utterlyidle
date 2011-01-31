package com.googlecode.utterlyidle.httpserver;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import static com.googlecode.totallylazy.callables.TimeCallable.calculateMilliseconds;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.utterlyidle.io.Url.url;
import static java.lang.System.nanoTime;

public class RestHandler implements HttpHandler {
    private final Application application;
    private final BasePath basePath;

    public RestHandler(Application application, BasePath basePath) {
        this.application = application;
        this.basePath = basePath;
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        Response response = handle(request(httpExchange));
        mapTo(response, httpExchange);
    }

    private Response handle(Request request) throws IOException {
        try {
            long start = nanoTime();
            Response response = application.handle(request);
            System.out.println(String.format("%s %s -> %s in %s msecs", request.method(), request.url(), response.status(), calculateMilliseconds(start, nanoTime())));
            return response;
        } catch (Exception e) {
            return exceptionResponse(request, e);
        }
    }

    private void mapTo(Response response, HttpExchange httpExchange) throws IOException {
        for (Pair<String, String> pair : response.headers()) {
            httpExchange.getResponseHeaders().add(pair.first(), pair.second());
        }
        byte[] bytes = response.bytes();
        httpExchange.sendResponseHeaders(response.status().code(), bytes.length);
        OutputStream responseBody = httpExchange.getResponseBody();
        responseBody.write(bytes);
    }


    private MemoryRequest request(HttpExchange httpExchange) {
        return Requests.request(
                httpExchange.getRequestMethod(),
                url(httpExchange.getRequestURI().toString()),
                convert(httpExchange.getRequestHeaders()),
                bytes(httpExchange.getRequestBody()),
                basePath);
    }

    private Response exceptionResponse(Request request, Exception e) throws IOException {
        System.err.println(String.format("%s %s -> %s", request.method(), request.url(), e));
        e.printStackTrace(System.err);
        Response response = response().status(Status.INTERNAL_SERVER_ERROR);
        PrintWriter writer = new PrintWriter(response.output());
        e.printStackTrace(writer);
        writer.close();
        return response;
    }

    public static HeaderParameters convert(Map<String, List<String>> requestHeaders) {
        HeaderParameters result = HeaderParameters.headerParameters();
        for (Map.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
            for (String value : entry.getValue()) {
                result.add(entry.getKey(), value);
            }
        }
        return result;
    }
}
