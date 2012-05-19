package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import static com.googlecode.utterlyidle.HttpHeaders.ACCEPT_ENCODING;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_ENCODING;
import static com.googlecode.utterlyidle.ResponseBuilder.modify;

public class GzipHandler implements HttpHandler {
    public static final String GZIP = "gzip";
    private final HttpHandler httpHandler;
    private final InternalRequestMarker marker;

    public GzipHandler(HttpHandler httpHandler, InternalRequestMarker marker) {
        this.httpHandler = httpHandler;
        this.marker = marker;
    }

    @Override
    public Response handle(Request request) throws Exception {
        Response response = httpHandler.handle(request);
        if (clientAcceptsGzip(request.headers()) && !marker.isInternal(request) && !response.entity().isStreaming()) {
            return modify(response).header(CONTENT_ENCODING, GZIP).entity(gzip(response.entity().asBytes())).build();
        }
        return response;
    }

    private boolean clientAcceptsGzip(final HeaderParameters headers) {
        return headers.contains(ACCEPT_ENCODING) && headers.getValue(ACCEPT_ENCODING).contains(GZIP);
    }

    private byte[] gzip(byte[] input) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        GZIPOutputStream outputStream = new GZIPOutputStream(stream);
        outputStream.write(input);
        outputStream.close();
        return stream.toByteArray();
    }
}
