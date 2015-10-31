package com.googlecode.utterlyidle;

import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_FOR;
import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_PROTO;
import static com.googlecode.utterlyidle.HttpMessage.Builder.header;
import static com.googlecode.utterlyidle.HttpMessage.Builder.modify;

public class RequestEnricher {
    private final ClientAddress clientAddress;
    private final String protocol;

    public static RequestEnricher requestEnricher(final ClientAddress clientAddress, String protocol){
        return new RequestEnricher(clientAddress,protocol);
    }
    private RequestEnricher(final ClientAddress clientAddress, String protocol) {
        this.clientAddress = clientAddress;
        this.protocol = protocol.toLowerCase();
    }

    public Request enrich(Request request){
        return withXForwardedProto(withXForwardedFor(request));
    }

    private Request withXForwardedProto(Request request) {
        request.headers();
        if (request.headers().contains(X_FORWARDED_PROTO)) return request;
        return modify(request, HttpMessage.Builder.header(X_FORWARDED_PROTO, protocol));
    }
    private Request withXForwardedFor(Request request) {
        request.headers();
        if (request.headers().contains(X_FORWARDED_FOR)) return request;
        return modify(request, HttpMessage.Builder.header(X_FORWARDED_FOR, clientAddress.value()));
    }
}
