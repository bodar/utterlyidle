package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicate;

import static com.googlecode.utterlyidle.Accept.accept;

public class ProducesMimeMatcher implements Predicate<Request> {
    private final String mimeType;

    public ProducesMimeMatcher(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean matches(Request request) {
       return accept(request).contains(mimeType);
    }

    public float matchQuality(Request request) {
       return accept(request).quality(mimeType);
    }

    public String mimeType() {
        return mimeType;
    }

    public static Predicate<HttpSignature> producesMatches(final Request request) {
        return new Predicate<HttpSignature>() {
            public boolean matches(HttpSignature httpSignature) {
                return new ProducesMimeMatcher(httpSignature.produces()).matches(request);
            }
        };
    }
}