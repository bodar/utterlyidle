package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.predicates.LogicalPredicate;

public class ConsumesMimeMatcher implements Predicate<Request> {
    private final Sequence<String> mimeTypes;

    public ConsumesMimeMatcher(Sequence<String> mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public boolean matches(final Request request) {
        if (mimeTypes.contains(MediaType.WILDCARD)) return true;
        return request.headers().valueOption(HttpHeaders.CONTENT_TYPE).exists(new LogicalPredicate<String>() {
            @Override
            public boolean matches(final String type) {
                return mimeTypes.exists(new Predicate<String>() {
                    public boolean matches(String mimeType) {
                        return type.startsWith(mimeType);
                    }
                });
            }
        });
    }

    public static Predicate<Binding> contentMatches(final Request request) {
        return new Predicate<Binding>() {
            public boolean matches(Binding binding) {
                return new ConsumesMimeMatcher(binding.consumes()).matches(request);
            }
        };
    }

}