package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import static com.googlecode.utterlyidle.Request.Builder.modify;

public class UnGzipHandler implements HttpClient {
    private final HttpHandler handler;

    public UnGzipHandler(final HttpHandler handler) {
        this.handler = handler;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        Response response = handler.handle(modify(request, Request.Builder.header(HttpHeaders.ACCEPT_ENCODING, "gzip")));
        if("gzip".equalsIgnoreCase(response.headers().getValue(HttpHeaders.CONTENT_ENCODING))) {
            return ResponseBuilder.modify(response).entity(gzipInputStream(response.entity().inputStream())).removeHeaders(HttpHeaders.CONTENT_ENCODING).build();
        }
        return response;
    }

    public static GZIPInputStream gzipInputStream(InputStream inputStream) throws IOException {
        if(inputStream instanceof GZIPInputStream) return (GZIPInputStream) inputStream;
        return new GZIPInputStream(inputStream);
    }
}

