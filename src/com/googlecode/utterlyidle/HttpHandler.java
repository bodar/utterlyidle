package com.googlecode.utterlyidle;

public interface HttpHandler {
    Response handle(final Request request) throws Exception;
}
