package com.googlecode.utterlyidle;

public interface ResponseHandler {
    void handle(Request request, Response response) throws Exception;
}

