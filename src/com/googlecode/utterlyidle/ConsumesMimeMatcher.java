package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;

public class ConsumesMimeMatcher implements Predicate<Request> {
    private final Sequence<String> mimeTypes;

    public ConsumesMimeMatcher(Sequence<String> mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public boolean matches(final Request request) {
        if (mimeTypes.contains(MediaType.WILDCARD)) {
            return true;
        }
        if (request.headers().contains(HttpHeaders.CONTENT_TYPE)) {
            return mimeTypes.exists(new Predicate<String>() {
                @Override
                public boolean matches(String other) {
                    return other.startsWith(request.headers().getValue(HttpHeaders.CONTENT_TYPE));
                }
            });
        }
        return true;
    }

    public static Predicate<Binding> contentMatches(final Request request) {
        return new Predicate<Binding>() {
            public boolean matches(Binding binding) {
                return new ConsumesMimeMatcher(binding.consumes()).matches(request);
            }
        };
    }

}