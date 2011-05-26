package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.*;
import com.googlecode.totallylazy.regex.Regex;

import java.util.regex.MatchResult;

import static com.googlecode.totallylazy.Callables.descending;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.MediaRange.sameValue;
import static java.lang.Float.valueOf;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.MediaType.WILDCARD;

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

    private Callable1<? super MediaRange, Float> mediaQuality() {
        return new Callable1<MediaRange, Float>() {
            public Float call(MediaRange mediaRange) throws Exception {
                return mediaRange.quality();
            }
        };
    }

    private Predicate<? super MediaRange> containedBy(final Sequence<String> values) {
        return new Predicate<MediaRange>() {
            public boolean matches(MediaRange mediaRange) {
                return values.exists(sameValue(mediaRange.value()));
            }
        };
    }


    public static Accept accept(String header) {
        Sequence<MediaRange> mediaRanges = regex.findMatches(header).map(new Callable1<MatchResult, MediaRange>() {
            public MediaRange call(MatchResult m) throws Exception {
                float quality = m.group(3) == null ? 1.0f : valueOf(m.group(3));
                return new MediaRange(m.group(1), quality);
            }
        });
        return new Accept(mediaRanges);
    }

    public static Accept accept(Request request) {
        if (request.headers().contains(ACCEPT)) {
            return accept(request.headers().getValue(ACCEPT));
        }
        return new Accept(sequence(new MediaRange(WILDCARD, 1.0f)));
    }

}