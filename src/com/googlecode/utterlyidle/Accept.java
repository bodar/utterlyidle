package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.functions.Callables;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.totallylazy.regex.Regex;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.functions.Callables.descending;
import static com.googlecode.totallylazy.predicates.Predicates.where;
import static com.googlecode.utterlyidle.HttpHeaders.ACCEPT;
import static com.googlecode.utterlyidle.MediaRange.convertWildCardsTo;
import static com.googlecode.utterlyidle.MediaRange.sameValue;
import static com.googlecode.utterlyidle.MediaType.WILDCARD;
import static java.lang.Float.valueOf;

public class Accept implements Predicate<String>{
    private static Regex regex = Regex.regex("([^,;\\s]+)(;\\s*q=([0-9\\.]+))?,?");
    private final Sequence<MediaRange> mediaRanges;

    public Accept(Sequence<MediaRange> mediaRanges) {
        this.mediaRanges = mediaRanges;
    }

    public boolean matches(final String value) {
        return mediaRanges.exists(where(Callables.<String>value(), sameValue(value)));
    }

    public boolean exists(final Sequence<String> values) {
        return values.exists(this);
    }

    public float quality(String value) {
        return quality(sequence(value));
    }

    public float quality(Sequence<String> values) {
        return mediaRanges.filter(containedBy(values)).
                sortBy(descending(mediaQuality())).
                headOption().
                map(mediaQuality()).
                getOrElse(1.0f);
    }

    private Function1<? super MediaRange, Float> mediaQuality() {
        return mediaRange -> mediaRange.quality();
    }

    private Predicate<? super MediaRange> containedBy(final Sequence<String> values) {
        return mediaRange -> values.exists(sameValue(mediaRange.value()));
    }


    public static Accept accept(String header) {
        Sequence<MediaRange> mediaRanges = regex.findMatches(header).map(m -> {
            float quality = m.group(3) == null ? 1.0f : valueOf(m.group(3));
            return new MediaRange(m.group(1), quality);
        });
        return new Accept(mediaRanges);
    }

    public static Accept accept(Request request) {
        if (request.headers().contains(ACCEPT)) {
            return accept(request.headers().getValue(ACCEPT));
        }
        return new Accept(sequence(new MediaRange(WILDCARD, 1.0f)));
    }

    public String bestMatch(Sequence<String> possibleContentTypes) {
        return mediaRanges.flatMap(convertWildCardsTo(possibleContentTypes)).
                filter(containedBy(possibleContentTypes)).
                sortBy(descending(mediaQuality())).
                head().value();
    }

}