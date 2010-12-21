package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;

import java.io.OutputStreamWriter;

public class WriteMessageToResponseHandler  implements ResponseHandler<Object> {
    private final String message;

    public WriteMessageToResponseHandler(String message) {
        this.message = message;
    }

    public void handle(Response response) throws Exception {
        OutputStreamWriter writer = new OutputStreamWriter(response.output());
        writer.write(message);
        writer.close();
    }
}