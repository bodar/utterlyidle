package com.googlecode.utterlyidle;

public class Id {
    private final String value;

    private Id(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static Id id(String value) {
        return new Id(value);
    }
}
