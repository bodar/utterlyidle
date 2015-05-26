package com.googlecode.utterlyidle.cookies;

public class PrefixedEncoding implements CookieEncoding {

    public static String PREFIX = "utterlyidle:v1:";

    private final CookieEncoding delegate;

    public PrefixedEncoding(CookieEncoding delegate) {
        this.delegate = delegate;
    }

    @Override
    public String encode(String input) {
        return PREFIX + delegate.encode(input);
    }

    @Override
    public String decode(String input) {
        if(input.startsWith(PREFIX)) {
            return delegate.decode(input.substring(PREFIX.length()));
        }
        return input;
    }
}
