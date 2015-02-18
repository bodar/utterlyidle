package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Value;

public class RequestTimeout implements Value<Integer> {
    private final Integer value;

    public RequestTimeout(Integer value) {
        this.value = value;
    }

    public static RequestTimeout requestTimeout(Integer timeoutMillis) {
        return new RequestTimeout(timeoutMillis);
    }

    @Override
    public Integer value() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
