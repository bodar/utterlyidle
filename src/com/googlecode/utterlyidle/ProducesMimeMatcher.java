package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;

import static com.googlecode.utterlyidle.Accept.accept;

public class ProducesMimeMatcher implements Predicate<Request> {
    private final Sequence<String> mimeTypes;

    public ProducesMimeMatcher(Sequence<String> mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public static Function1<Binding, Float> quality(final Request request) {
        return binding -> quality(request, binding);
    }

    public static float quality(Request request, Binding firstSignature) {
        return new ProducesMimeMatcher(firstSignature.produces()).matchQuality(request);
    }

    public boolean matches(Request request) {
       return accept(request).exists(mimeTypes);
    }

    public float matchQuality(Request request) {
       return accept(request).quality(mimeTypes);
    }

    public Sequence<String> mimeType() {
        return mimeTypes;
    }

    public static Predicate<Binding> producesMatches(final Request request) {
        return binding -> new ProducesMimeMatcher(binding.produces()).matches(request);
    }
}