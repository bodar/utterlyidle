package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Value;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.predicates.Predicate;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.MediaType.WILDCARD;

public class MediaRange implements Value<String> {
    private static final String SUBTYPE_WILDCARD = "*";

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
            if (SUBTYPE_WILDCARD.equals(subType(mimeType)) || SUBTYPE_WILDCARD.equals(subType(value))) {
                return primaryType(mimeType).equals(primaryType(value));
            }
            return mimeType.equals(value);
        };
    }

    private static String subType(final String mimeType) {
        String[] parts = mimeType.split("/");
        return parts.length > 1 ? parts[1] : null;
    }

    private static String primaryType(final String mimeType) {
        String[] parts = mimeType.split("/");
        return parts[0];
    }

    public static Function1<? super MediaRange, Iterable<MediaRange>> convertWildCardsTo(final Sequence<String> possibleContentTypes) {
        return mediaRange -> {
            if (mediaRange.value().equals(WILDCARD)
                    || SUBTYPE_WILDCARD.equals(subType(mediaRange.value()))) {
                return possibleContentTypes.map(toMediaType(mediaRange.quality()));
            }
            return sequence(mediaRange);
        };
    }

    private static Function1<? super String, MediaRange> toMediaType(final float quality) {
        return value1 -> new MediaRange(value1, quality);
    }


}
