package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.StreamingOutput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StreamingOutputHandler implements ResponseHandler {
    public Response handle(Response response) throws IOException {
        StreamingOutput entity = (StreamingOutput) response.entity();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        entity.write(outputStream);
        response.bytes(outputStream.toByteArray());
        return response;
    }
}
