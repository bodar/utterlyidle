package com.googlecode.utterlyidle;

public class Cookie {
    private final String name;
    private final String value;

    public static Cookie cookie(String name, String value) {
        return new Cookie(name, value);
    }

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String toHttpHeader() {
        return String.format("%s=%s;", name, value);
    }
}
