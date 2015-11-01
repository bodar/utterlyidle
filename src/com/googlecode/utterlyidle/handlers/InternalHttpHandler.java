package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.First;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.functions.Callables;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.totallylazy.predicates.Predicates;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.HttpMessage;
import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;
import static com.googlecode.totallylazy.predicates.Predicates.by;
import static com.googlecode.utterlyidle.HttpHeaders.AUTHORIZATION;
import static com.googlecode.utterlyidle.HttpHeaders.COOKIE;
import static com.googlecode.utterlyidle.HttpMessage.Builder.header;
import static com.googlecode.totallylazy.functions.Functions.modify;

public class InternalHttpHandler implements HttpHandler {
    private static final Sequence<String> HEADER_WHITE_LIST = sequence(COOKIE, AUTHORIZATION);

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
        Request requestWithOriginalHeaders = requestWithOriginalHeaders(request);
        return application.handle(marker.markAsInternal(requestWithOriginalHeaders));
    }

    private Request requestWithOriginalHeaders(Request request) {
        Predicate<First<String>> inWhitelist = by(Callables.<String>first(), Predicates.or(HEADER_WHITE_LIST.map(equalIgnoringCase())));
        return modify(request, HttpMessage.Builder.header(originalRequest.headers().filter(inWhitelist)));
    }
}