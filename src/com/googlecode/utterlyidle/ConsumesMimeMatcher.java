package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicate;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class ConsumesMimeMatcher implements Predicate<Request> {
    private final String mimeType;

    public ConsumesMimeMatcher(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean matches(Request request) {
        if (mimeType.equals(MediaType.WILDCARD)) {
            return true;
        }
        if (request.headers().contains(HttpHeaders.CONTENT_TYPE)) {
            return request.headers().getValue(HttpHeaders.CONTENT_TYPE).equals(mimeType);
        }
        return true;
    }

    public static Predicate<HttpSignature> contentMatches(final Request request) {
        return new Predicate<HttpSignature>() {
            public boolean matches(HttpSignature httpSignature) {
                return new ConsumesMimeMatcher(httpSignature.consumes()).matches(request);
            }
        };
    }

}