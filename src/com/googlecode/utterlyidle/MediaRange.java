package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Value;

import static com.googlecode.totallylazy.Sequences.sequence;
import static javax.ws.rs.core.MediaType.WILDCARD;

public class MediaRange implements Value<String>{
    private final String value;
    private final float quality;

    public MediaRange(String value, float quality) {
        this.value = value;
        this.quality = quality;
    }

    public String value() {
        return value;
    }

    public float quality() {
        return quality;
    }

    public static Predicate<String> sameValue(final String value) {
        return new Predicate<String>() {
            public boolean matches(String mimeType) {
                if (mimeType.equals(WILDCARD) || value.equals(WILDCARD)) {
                    return true;
                }
                return mimeType.equals(value);
            }
        };
    }

    public static Callable1<? super MediaRange, Iterable<MediaRange>> convertWildCardsTo(final Sequence<String> possibleContentTypes) {
        return new Callable1<MediaRange, Iterable<MediaRange>>() {
            public Iterable<MediaRange> call(MediaRange mediaRange) throws Exception {
                if(mediaRange.value().equals(WILDCARD)){
                    return possibleContentTypes.map(toMediaType(mediaRange.quality()));
                }
                return sequence(mediaRange);
            }
        };
    }

    private static Callable1<? super String, MediaRange> toMediaType(final float quality) {
        return new Callable1<String, MediaRange>() {
            public MediaRange call(String value) throws Exception {
                return new MediaRange(value, quality);
            }
        };
    }


}
