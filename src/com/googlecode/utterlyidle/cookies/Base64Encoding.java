package com.googlecode.utterlyidle.cookies;

import com.googlecode.totallylazy.security.Base64;

public class Base64Encoding implements CookieEncoding {

    Base64Encoding() { }

    @Override
    public String encode(String input) {
        return Base64.encode(input.getBytes());
    }

    @Override
    public String decode(String input) {
        return new String(Base64.decode(input));
    }
}
