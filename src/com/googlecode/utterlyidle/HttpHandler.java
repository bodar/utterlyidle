package com.googlecode.utterlyidle;

public interface HttpHandler {
    void handle(final Request request, final Response response) throws Exception;
}
