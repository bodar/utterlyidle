package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Value;

public class MediaRange implements Value<String>{
    private final String value;
    public final float quality;

    public MediaRange(String value, float quality) {
        this.value = value;
        this.quality = quality;
    }

    public String value() {
        return value;
    }
}
