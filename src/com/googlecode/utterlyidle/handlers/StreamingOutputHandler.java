package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.yadic.Resolver;

import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;

public class StreamingOutputHandler implements ResponseHandler<StreamingOutput> {
    public void handle(StreamingOutput value, Resolver resolver, Response response) throws IOException {
        value.write(response.output());
    }
}
