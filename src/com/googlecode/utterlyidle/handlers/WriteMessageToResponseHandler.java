package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.Status;
import com.googlecode.yadic.Resolver;

import javax.ws.rs.core.HttpHeaders;
import java.io.OutputStreamWriter;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;

public class WriteMessageToResponseHandler  implements ResponseHandler<Object> {
    private final String message;

    public WriteMessageToResponseHandler(String message) {
        this.message = message;
    }

    public void handle(Object value, Resolver resolver, Response response) throws Exception {
        OutputStreamWriter writer = new OutputStreamWriter(response.output());
        writer.write(message);
        writer.close();
    }
}