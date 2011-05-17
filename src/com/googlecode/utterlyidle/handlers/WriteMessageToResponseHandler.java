package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;

import java.io.OutputStreamWriter;

import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Runnables.write;


public class WriteMessageToResponseHandler implements ResponseHandler {
    private final String message;

    public WriteMessageToResponseHandler(String message) {
        this.message = message;
    }

    public Response handle(final Response response) throws Exception {
        using(new OutputStreamWriter(response.output()), write(message));
        return response;
    }

}