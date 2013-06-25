package com.googlecode.utterlyidle.modules;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.Status;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.googlecode.totallylazy.Arrays.list;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.HttpHeaders.CACHE_CONTROL;
import static com.googlecode.utterlyidle.HttpHeaders.Content_MD5;
import static com.googlecode.utterlyidle.HttpHeaders.DATE;
import static com.googlecode.utterlyidle.HttpHeaders.ETAG;
import static com.googlecode.utterlyidle.HttpHeaders.EXPIRES;
import static com.googlecode.utterlyidle.HttpHeaders.IF_NONE_MATCH;
import static com.googlecode.utterlyidle.HttpHeaders.LAST_MODIFIED;
import static com.googlecode.utterlyidle.HttpHeaders.SET_COOKIE;
import static com.googlecode.utterlyidle.Response.methods.header;
import static com.googlecode.utterlyidle.ResponseBuilder.modify;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.Status.NOT_MODIFIED;
import static com.googlecode.utterlyidle.annotations.HttpMethod.GET;
import static com.googlecode.utterlyidle.modules.Digest.md5;

public class EtagHandler implements HttpHandler {
    public static final List<String> safeHeaders = new CopyOnWriteArrayList<String>(list(DATE, LAST_MODIFIED, CACHE_CONTROL, EXPIRES, SET_COOKIE));
    private final HttpHandler httpHandler;

    public EtagHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    @Override
    public Response handle(Request request) throws Exception {
        Response response = httpHandler.handle(request);
        if (!request.method().equals(GET) || !response.status().equals(Status.OK) || response.entity().length() < 0) {
            return response;
        }

        Digest digest = md5(response.entity().asBytes());
        String etag = strongEtag(digest);
        if (etag.equals(request.headers().getValue(IF_NONE_MATCH))) {
            return copySafeHeaders(response, response(NOT_MODIFIED)).build();
        }
        return modify(response).header(ETAG, etag).header(Content_MD5, digest.asBase64()).build();
    }

    private ResponseBuilder copySafeHeaders(final Response source, ResponseBuilder destination) {
        return sequence(safeHeaders).fold(destination, copyFrom(source));
    }

    private static Callable2<ResponseBuilder, String, ResponseBuilder> copyFrom(final Response source) {
        return new Callable2<ResponseBuilder, String, ResponseBuilder>() {
            @Override
            public ResponseBuilder call(ResponseBuilder destination, String header) throws Exception {
                return destination.header(header, header(source, header));
            }
        };
    }

    private String strongEtag(Digest digest) {
        return "\"" + digest.asHex() + "\"";
    }

}
