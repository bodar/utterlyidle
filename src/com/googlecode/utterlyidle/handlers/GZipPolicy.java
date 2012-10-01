package com.googlecode.utterlyidle.handlers;

public class GZipPolicy extends Policy<GZipPolicy> {
    private GZipPolicy() {
    }

    public static GZipPolicy gZipPolicy() {
        return new GZipPolicy();
    }

    @Override
    protected GZipPolicy self() {
        return this;
    }
}
