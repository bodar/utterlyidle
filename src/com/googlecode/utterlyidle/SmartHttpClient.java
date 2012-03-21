package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.handlers.HttpClient;
import com.googlecode.utterlyidle.handlers.InternalHttpHandler;

public class SmartHttpClient implements HttpClient {
    private final InternalHttpHandler internalHttpHandler;
    private final HttpClient httpClient;

    public SmartHttpClient(InternalHttpHandler internalHttpHandler, HttpClient httpClient) {
        this.internalHttpHandler = internalHttpHandler;
        this.httpClient = httpClient;
    }

    @Override
    public Response handle(Request request) throws Exception {
        if(request.uri().isFullyQualified()){
            return httpClient.handle(request);
        }
        return internalHttpHandler.handle(request);
    }
}
