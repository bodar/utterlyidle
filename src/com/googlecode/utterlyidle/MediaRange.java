package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Value;

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
}
