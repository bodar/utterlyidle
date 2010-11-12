package com.googlecode.utterlyidle.cookies;

import com.googlecode.totallylazy.Sequence;

import static com.googlecode.totallylazy.Sequences.sequence;

public class Cookie {
    private final CookieName name;
    private final String value;
    private static Sequence<CookieAttribute> attributes;

    public static Cookie cookie(CookieName name, String value, CookieAttribute... attributes) {
        return new Cookie(name, value, attributes);
    }

    public Cookie(CookieName name, String value, CookieAttribute... attributes) {
        this.name = name;
        this.value = value;
        this.attributes = sequence(attributes);
    }

    public String getValue() {
        return value;
    }

    public CookieName getName() {
        return name;
    }

    public String toHttpHeader() {
        final String cookieValue = String.format("%s=\"%s\"; ", name, value.replace("\"", "\\\""));
        final String attributes = sequence(Cookie.attributes).toString("; ");
        return cookieValue + attributes;
    }
}
