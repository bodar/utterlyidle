package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.StreamingWriter;
import com.googlecode.yadic.Resolver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class StreamingWriterHandler implements ResponseHandler<StreamingWriter> {
    public void handle(StreamingWriter value, Resolver resolver, Response response) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(response.output());
        value.write(writer);
        writer.close();
    }
}
