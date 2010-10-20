package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;

import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;

public class StreamingOutputHandler implements ResponseHandler<StreamingOutput> {
    public void handle(StreamingOutput value, Response response) throws IOException {
        value.write(response.output());
    }
}
