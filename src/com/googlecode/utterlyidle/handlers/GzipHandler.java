package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.LazyException;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.HttpHeaders.ACCEPT_ENCODING;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_ENCODING;
import static com.googlecode.utterlyidle.ResponseBuilder.modify;

public class GzipHandler implements HttpHandler {
    public static final String GZIP = "gzip";
    private final HttpHandler httpHandler;
    private final InternalRequestMarker marker;
    private final GZipPolicy gZipPolicy;

    public GzipHandler(HttpHandler httpHandler, InternalRequestMarker marker, GZipPolicy gZipPolicy) {
        this.httpHandler = httpHandler;
        this.marker = marker;
        this.gZipPolicy = gZipPolicy;
    }

    @Override
    public Response handle(Request request) throws Exception {
        Response response = httpHandler.handle(request);
        if (clientAcceptsGZip(request.headers()) &&
                !marker.isInternal(request) &&
                !response.entity().isStreaming() &&
                gZipPolicy.matches(pair(request, response))) {
            return modify(response).header(CONTENT_ENCODING, GZIP).entity(gzip(response.entity().asBytes())).build();
        }
        return response;
    }

    public static boolean clientAcceptsGZip(final HeaderParameters headers) {
        return headers.contains(ACCEPT_ENCODING) && headers.getValue(ACCEPT_ENCODING).contains(GZIP);
    }

    public static boolean isGZipped(final HeaderParameters headers) {
        return headers.contains(CONTENT_ENCODING) && headers.getValue(CONTENT_ENCODING).contains(GZIP);
    }

    public static byte[] gzip(byte[] data) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        GZIPOutputStream outputStream = new GZIPOutputStream(stream);
        outputStream.write(data);
        outputStream.close();
        return stream.toByteArray();
    }

    public static byte[] ungzip(byte[] input) {
        try {
            return bytes(new GZIPInputStream(new ByteArrayInputStream(input)));
        } catch (IOException e) {
            throw LazyException.lazyException(e);
        }
    }

}
