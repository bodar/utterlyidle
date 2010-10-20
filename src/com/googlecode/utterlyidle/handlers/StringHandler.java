package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;

import java.io.IOException;

public class StringHandler implements ResponseHandler<String> {
    public void handle(String value, Response response) throws IOException {
        response.write(value);
    }
}
