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

    public static Predicate<Binding> producesMatches(final Request request) {
        return new Predicate<Binding>() {
            public boolean matches(Binding binding) {
                return new ProducesMimeMatcher(binding.produces()).matches(request);
            }
        };
    }
}