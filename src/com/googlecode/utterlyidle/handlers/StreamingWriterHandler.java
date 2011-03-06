package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.StreamingWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.googlecode.totallylazy.Closeables.using;


public class StreamingWriterHandler implements ResponseHandler {
    public Response handle(final Response response) throws IOException {
        return using(new OutputStreamWriter(response.output()), write(response));
    }

    public static Callable1<Writer, Response> write(final Response response) {
        return new Callable1<Writer, Response>() {
            public Response call(Writer writer) throws Exception {
                StreamingWriter streamingWriter = (StreamingWriter) response.entity();
                streamingWriter.write(writer);
                return response;
            }
        };
    }
}
