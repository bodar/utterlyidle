package com.googlecode.utterlyidle.cookies;

public class UrlEncodingTest extends CookieEncodingContract {

    @Override
    protected String input() {
        return "Hōb Nőḃ";
    }

    @Override
    protected String expectedOutput() {
        return "H%C5%8Db+N%C5%91%E1%B8%83";
    }

    @Override
    protected CookieEncoding encoding() { return new UrlEncoding(); }
}
