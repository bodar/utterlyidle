package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.StreamingWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class StreamingWriterHandler implements ResponseHandler {
    public Response handle(final Response response) throws IOException {
        StreamingWriter streamingWriter = (StreamingWriter) response.entity();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        streamingWriter.write(writer);
        writer.close();
        return response.bytes(stream.toByteArray());

    }
}
