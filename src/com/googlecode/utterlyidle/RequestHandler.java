package com.googlecode.utterlyidle;

public interface RequestHandler {
    void handle(Request request, Response response) throws Exception;
}
