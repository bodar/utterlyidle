package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;

import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;

public class StreamingOutputHandler implements ResponseHandler {
    public Response handle(Response response) throws IOException {
        StreamingOutput entity = (StreamingOutput) response.entity();
        entity.write(response.output());
        return response;
    }
}
