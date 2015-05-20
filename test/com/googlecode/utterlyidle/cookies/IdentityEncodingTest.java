package com.googlecode.utterlyidle.cookies;

public class IdentityEncodingTest extends CookieEncodingContract {

    @Override
    protected String input() {
        return "anything";
    }

    @Override
    protected String expectedOutput() {
        return input();
    }

    @Override
    protected CookieEncoding encoding() {
        return new IdentityEncoding();
    }
}
