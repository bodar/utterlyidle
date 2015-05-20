package com.googlecode.utterlyidle.cookies;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Rfc2616;

import static com.googlecode.totallylazy.Sequences.sequence;
import static java.lang.String.format;

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

    @Override
    public String toString() {
        final String cookieValue = format("%s=%s; ", name, Rfc2616.toQuotedString(value));
        final String attributes = this.attributes.toString("; ");
        return cookieValue + attributes;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Cookie && other.toString().equals(toString());
    }
}