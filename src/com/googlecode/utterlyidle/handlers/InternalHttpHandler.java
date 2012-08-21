package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Callables;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.totallylazy.Predicates.in;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.HttpHeaders.COOKIE;
import static com.googlecode.utterlyidle.RequestBuilder.modify;

public class InternalHttpHandler implements HttpHandler {
    private static final Sequence<String> HEADER_WHITE_LIST = sequence(COOKIE);

    private final InternalRequestMarker marker;
    private final Application application;
    private final Request originalRequest;

    public InternalHttpHandler(InternalRequestMarker marker, Application application, Request originalRequest) {
        this.marker = marker;
        this.application = application;
        this.originalRequest = originalRequest;
    }

    @Override
    public Response handle(Request request) throws Exception {
        Request requestWithHeaders = sequence(originalRequest.headers()).filter(where(Callables.<String>first(), in(HEADER_WHITE_LIST))).fold(modify(request), addHeader()).build();
        return application.handle(marker.markAsInternal(requestWithHeaders));
    }

    private Callable2<RequestBuilder, Pair<String, String>, RequestBuilder> addHeader() {
        return new Callable2<RequestBuilder, Pair<String, String>, RequestBuilder>() {
            @Override
            public RequestBuilder call(RequestBuilder requestBuilder, Pair<String, String> headerPair) throws Exception {
                return requestBuilder.header(headerPair.first(), headerPair.second());
            }
        };
    }
}