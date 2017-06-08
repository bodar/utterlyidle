package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.functions.Function2;
import com.googlecode.totallylazy.security.Digest;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.googlecode.totallylazy.Arrays.list;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.security.Digest.md5;
import static com.googlecode.utterlyidle.HttpHeaders.CACHE_CONTROL;
import static com.googlecode.utterlyidle.HttpHeaders.Content_MD5;
import static com.googlecode.utterlyidle.HttpHeaders.DATE;
import static com.googlecode.utterlyidle.HttpHeaders.ETAG;
import static com.googlecode.utterlyidle.HttpHeaders.EXPIRES;
import static com.googlecode.utterlyidle.HttpHeaders.IF_NONE_MATCH;
import static com.googlecode.utterlyidle.HttpHeaders.LAST_MODIFIED;
import static com.googlecode.utterlyidle.HttpHeaders.SET_COOKIE;
import static com.googlecode.utterlyidle.Status.NOT_MODIFIED;
import static com.googlecode.utterlyidle.annotations.HttpMethod.GET;

public class EtagHandler implements HttpHandler {
    public static final List<String> safeHeaders = new CopyOnWriteArrayList<String>(list(DATE, LAST_MODIFIED, CACHE_CONTROL, EXPIRES, SET_COOKIE));
    private final HttpHandler httpHandler;

    public EtagHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    @Override
    public Response handle(Request request) throws Exception {
        Response response = httpHandler.handle(request);
        if (!request.method().equals(GET) || !response.status().equals(Status.OK) || response.entity().length().isEmpty()) {
            return response;
        }

        Digest digest = md5(response.entity().toBytes());
        String etag = strongEtag(digest);
        if (etag.equals(request.headers().getValue(IF_NONE_MATCH))) {
            return copySafeHeaders(response, Response.response(NOT_MODIFIED));
        }
        return response.header(ETAG, etag).header(Content_MD5, digest.toBase64());
    }

    private Response copySafeHeaders(final Response source, Response destination) {
        return sequence(safeHeaders).fold(destination, copyFrom(source));
    }

    private static Function2<Response, String, Response> copyFrom(final Response source) {
        return (destination, header) -> destination.header(header, source.header(header).getOrNull());
    }

    private String strongEtag(Digest digest) {
        return "\"" + digest.toHex() + "\"";
    }

}
