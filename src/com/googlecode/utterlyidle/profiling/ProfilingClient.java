package com.googlecode.utterlyidle.profiling;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.HttpClient;

import static com.googlecode.totallylazy.callables.TimeCallable.calculateMilliseconds;
import static com.googlecode.utterlyidle.handlers.HttpClient.methods.httpClient;

public class ProfilingClient implements HttpClient {
    private final HttpClient httpHandler;
    private final ProfilingData profilingData;

    public ProfilingClient(HttpClient httpHandler, ProfilingData profilingData) {
        this.httpHandler = httpHandler;
        this.profilingData = profilingData;
    }

    public static ProfilingClient profile(final HttpHandler httpHandler, ProfilingData profilingData) {
        return new ProfilingClient(httpClient(httpHandler), profilingData);
    }

    @Override
    public Response handle(Request request) throws Exception {
        long start = System.nanoTime();
        Response response = httpHandler.handle(request);
        profilingData.add(request, response, calculateMilliseconds(start, System.nanoTime()));
        return response;
    }
}
