package com.googlecode.utterlyidle.cookies;

public class IdentityEncoding implements CookieEncoding {

    IdentityEncoding() { }

    @Override
    public String encode(String input) {
        return input;
    }

    @Override
    public String decode(String input) {
        return input;
    }
}
