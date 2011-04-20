package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicate;

import javax.ws.rs.core.HttpHeaders;

import static com.googlecode.utterlyidle.Accept.accept;

public class ProducesMimeMatcher implements Predicate<Request> {
    private final String mimeType;

    public ProducesMimeMatcher(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean matches(Request request) {
        if (request.headers().contains(HttpHeaders.ACCEPT)) {
            return accept(request.headers().getValue(HttpHeaders.ACCEPT)).contains(mimeType);
        }
        return true;
    }

    public float matchQuality(Request request) {
        if (request.headers().contains(HttpHeaders.ACCEPT)) {
            return accept(request.headers().getValue(HttpHeaders.ACCEPT)).quality(mimeType);
        }
        return 1.0f;
    }

    public String mimeType() {
        return mimeType;
    }
}