package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Value;

public class CachePolicy extends Policy<CachePolicy> implements Value<Integer> {
    private final int seconds;

    public CachePolicy(int seconds) {
        this.seconds = seconds;
    }

    public static CachePolicy cachePolicy(final int seconds) {
        return new CachePolicy(seconds);
    }

    @Override
    public Integer value() {
        return seconds;
    }

    @Override
    protected CachePolicy self() {
        return this;
    }
}
