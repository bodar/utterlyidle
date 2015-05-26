package com.googlecode.utterlyidle.cookies;

import java.net.URLDecoder;
import java.net.URLEncoder;

import static com.googlecode.totallylazy.LazyException.lazyException;

public class UrlEncoding implements CookieEncoding {

    private static final String characterEncoding = "UTF-8";

    UrlEncoding() {
    }

    @Override
    public String encode(String input) {
        try {
            return URLEncoder.encode(input, characterEncoding);
        } catch (Exception e) {
            throw lazyException(e);
        }
    }

    @Override
    public String decode(String input) {
        try {
            return URLDecoder.decode(input, characterEncoding);
        } catch (Exception e) {
            throw lazyException(e);
        }
    }
}
