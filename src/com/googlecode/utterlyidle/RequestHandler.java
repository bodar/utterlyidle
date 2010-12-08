package com.googlecode.utterlyidle;

public interface RequestHandler {
    void handle(final Request request, final Response response) throws Exception;
}
