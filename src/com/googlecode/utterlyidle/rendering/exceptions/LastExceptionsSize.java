package com.googlecode.utterlyidle.rendering.exceptions;

import com.googlecode.totallylazy.Value;
import com.googlecode.utterlyidle.UtterlyIdleProperties;

public class LastExceptionsSize implements Value<Integer> {
    public static final String KEY = "last.exceptions.size";
    public static final int DEFAULT = 20;
    private final int value;

    public LastExceptionsSize(final int value) {
        this.value = value;
    }

    public LastExceptionsSize(UtterlyIdleProperties utterlyIdleProperties) {
        this(Integer.valueOf(utterlyIdleProperties.getProperty(KEY, String.valueOf(DEFAULT))));
    }

    @Override
    public Integer value() {
        return value;
    }
}
