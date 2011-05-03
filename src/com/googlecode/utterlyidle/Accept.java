package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.regex.Regex;

import java.util.regex.MatchResult;

import static com.googlecode.totallylazy.Sequences.sequence;
import static java.lang.Float.valueOf;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.MediaType.WILDCARD;

public class Accept {
    private static Regex regex = Regex.regex("([^,;\\s]+)(;\\s*q=([0-9\\.]+))?,?");
    private final Sequence<MediaRange> mediaRanges;

    public Accept(Sequence<MediaRange> mediaRanges) {
        this.mediaRanges = mediaRanges;
    }

    public boolean contains(final String value) {
        return mediaRanges.exists(sameValue(value));
    }

    public float quality(String value) {
        return mediaRanges.find(sameValue(value)).get().quality();
    }

    private Predicate<MediaRange> sameValue(final String value) {
        return new Predicate<MediaRange>() {
            public boolean matches(MediaRange mediaRange) {
                if (mediaRange.value().equals(WILDCARD)) {
                    return true;
                }
                return mediaRange.value().equals(value);
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
        return new Accept(sequence(new MediaRange("*/*", 1.0f)));
    }
}