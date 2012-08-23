package com.googlecode.utterlyidle.cookies;

import com.googlecode.totallylazy.Sequence;

import static com.googlecode.totallylazy.Sequences.sequence;

public class Cookie {
    private final String name;
    private final String value;
    private final Sequence<CookieAttribute> attributes;

    public static Cookie cookie(String name, String value, CookieAttribute... attributes) {
        return new Cookie(name, value, attributes);
    }

    public Cookie(String name, String value, CookieAttribute... attributes) {
        this.name = name;
        this.value = value;
        this.attributes = sequence(attributes);
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }

    public Iterable<CookieAttribute> attributes() {
        return attributes;
    }
}