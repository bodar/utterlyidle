package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Value;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.predicates.Predicate;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.MediaType.WILDCARD;

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
        return mimeType -> {
            if (mimeType.equals(WILDCARD) || value.equals(WILDCARD)) {
                return true;
            }
            return mimeType.equals(value);
        };
    }

    public static Function1<? super MediaRange, Iterable<MediaRange>> convertWildCardsTo(final Sequence<String> possibleContentTypes) {
        return mediaRange -> {
            if(mediaRange.value().equals(WILDCARD)){
                return possibleContentTypes.map(toMediaType(mediaRange.quality()));
            }
            return sequence(mediaRange);
        };
    }

    private static Function1<? super String, MediaRange> toMediaType(final float quality) {
        return value1 -> new MediaRange(value1, quality);
    }


}
