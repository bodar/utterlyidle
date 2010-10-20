package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.StreamingWriter;

import java.io.IOException;

public class StreamingWriterHandler implements ResponseHandler<StreamingWriter> {
    public void handle(StreamingWriter value, Response response) throws IOException {
        value.write(response.writer());
    }
}
