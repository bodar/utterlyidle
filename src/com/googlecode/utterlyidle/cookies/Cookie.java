package com.googlecode.utterlyidle.cookies;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Rfc2616;

import static com.googlecode.totallylazy.Sequences.sequence;

public class Cookie {
    private final String value;
    private static Sequence<CookieAttribute> attributes;

    public static Cookie cookie(String value, CookieAttribute... attributes) {
        return new Cookie(value, attributes);
    }

    public Cookie(String value, CookieAttribute... attributes) {
        this.value = value;
        this.attributes = sequence(attributes);
    }

    public String value() {
        return value;
    }

    public static Iterable<CookieAttribute> attributes() {
        return attributes;
    }
}
