package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static com.googlecode.utterlyidle.MediaRange.sameValue;

public class ConsumesMimeMatcher implements Predicate<Request> {
    private final Sequence<String> mimeTypes;

    public ConsumesMimeMatcher(Sequence<String> mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public boolean matches(Request request) {
        if (mimeTypes.contains(MediaType.WILDCARD)) {
            return true;
        }
        if (request.headers().contains(HttpHeaders.CONTENT_TYPE)) {
            return mimeTypes.contains(request.headers().getValue(HttpHeaders.CONTENT_TYPE));
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