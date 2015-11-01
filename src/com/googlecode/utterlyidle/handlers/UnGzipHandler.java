package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.HttpMessage;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import static com.googlecode.totallylazy.functions.Functions.modify;

public class UnGzipHandler implements HttpClient {
    private final HttpHandler handler;

    public UnGzipHandler(final HttpHandler handler) {
        this.handler = handler;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        Response response = handler.handle(modify(request, HttpMessage.Builder.header(HttpHeaders.ACCEPT_ENCODING, "gzip")));
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

