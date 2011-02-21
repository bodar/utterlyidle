package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.StreamingWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class StreamingWriterHandler implements ResponseHandler {
    public Response handle(Response response) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(response.output());
        StreamingWriter value = (StreamingWriter) response.entity();
        value.write(writer);
        writer.close();
        return response;
    }
}
