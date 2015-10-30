package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.security.GZip;
import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;

import java.io.IOException;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.numbers.Numbers.greaterThan;
import static com.googlecode.utterlyidle.HttpHeaders.ACCEPT_ENCODING;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_ENCODING;
import static com.googlecode.utterlyidle.HttpHeaders.VARY;
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
        ResponseBuilder builder = modify(response).header(VARY, ACCEPT_ENCODING);
        if (clientAcceptsGZip(request.headers()) &&
                !marker.isInternal(request) &&
                response.entity().length().is(greaterThan(0)) &&
                gZipPolicy.matches(pair(request, response))) {
            return builder.header(CONTENT_ENCODING, GZIP).entity(GZip.gzip(response.entity().asBytes())).build();
        }
        return builder.build();
    }

    public static boolean clientAcceptsGZip(final HeaderParameters headers) {
        return headers.contains(ACCEPT_ENCODING) && headers.getValue(ACCEPT_ENCODING).contains(GZIP);
    }

    public static boolean isGZipped(final HeaderParameters headers) {
        return headers.contains(CONTENT_ENCODING) && headers.getValue(CONTENT_ENCODING).contains(GZIP);
    }

    @Deprecated // please use GZip.gzip in TotallyLazy
    public static byte[] gzip(byte[] data) throws IOException {
        return GZip.gzip(data);
    }

    @Deprecated // please use GZip.ungzip in TotallyLazy
    public static byte[] ungzip(byte[] input) {
        return GZip.ungzip(input);
    }

}
