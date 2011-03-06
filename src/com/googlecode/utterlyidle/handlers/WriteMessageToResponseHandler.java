package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.LazyException;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.googlecode.totallylazy.Runnables.write;
import static com.googlecode.totallylazy.Closeables.using;


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