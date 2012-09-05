package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Callables;
import com.googlecode.totallylazy.First;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.totallylazy.Predicates.by;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;
import static com.googlecode.utterlyidle.HttpHeaders.COOKIE;

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
        Request requestWithOriginalHeaders = requestWithOriginalHeaders(request);
        return application.handle(marker.markAsInternal(requestWithOriginalHeaders));
    }

    private Request requestWithOriginalHeaders(Request request) {
        Predicate<First<String>> inWhitelist = by(Callables.<String>first(), Predicates.or(HEADER_WHITE_LIST.map(equalIgnoringCase())));
        Sequence<Pair<String, String>> headersToAdd = originalRequest.headers().filter(inWhitelist);

        return headersToAdd.fold(request, addHeader());
    }

    private Callable2<Request, Pair<String, String>, Request> addHeader() {
        return new Callable2<Request, Pair<String, String>, Request>() {
            @Override
            public Request call(Request request, Pair<String, String> headerPair) throws Exception {
                return RequestBuilder.modify(request).header(headerPair.first(), headerPair.second()).build();
            }
        };
    }
}