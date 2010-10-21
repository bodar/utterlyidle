package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.Status;

import java.io.IOException;

public class ExceptionHandler implements ResponseHandler<Exception> {
    private final Status status;

    public ExceptionHandler(Status status) {
        this.status = status;
    }

    public void handle(Exception value, Response response) throws IOException {
        response.code(status);
    }
}
