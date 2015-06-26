package com.googlecode.utterlyidle.cookies;

import com.googlecode.totallylazy.Sequence;

import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Sequences.sequence;

public class CookieBuilder implements Callable<Cookie> {

    public static CookieBuilder modify(Cookie cookie) {
        return new CookieBuilder(cookie);
    }

    private String name;
    private String value;
    private Sequence<CookieAttribute> attributes = sequence();

    private CookieBuilder(Cookie cookie) {
        this.name = cookie.name();
        this.value = cookie.value();

        sequence(cookie.attributes()).fold(this, (cookieBuilder, cookieAttribute) -> cookieBuilder.attribute(cookieAttribute));
    }

    public CookieBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CookieBuilder value(String value) {
        this.value = value;
        return this;
    }

    public CookieBuilder attribute(CookieAttribute attribute) {
        this.attributes = this.attributes.append(attribute);
        return this;
    }

    @Override
    public Cookie call() throws Exception {
        return build();
    }

    public Cookie build() {
        return Cookie.cookie(name, value, attributes.toArray(new CookieAttribute[attributes.size()]));
    }
}
