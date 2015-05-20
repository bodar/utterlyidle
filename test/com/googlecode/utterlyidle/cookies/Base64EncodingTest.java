package com.googlecode.utterlyidle.cookies;

public class Base64EncodingTest extends CookieEncodingContract {

    @Override
    protected String input() {
        return "Hōb Nőḃ";
    }

    @Override
    protected String expectedOutput() {
        return "SMWNYiBOxZHhuIM=";
    }

    @Override
    protected CookieEncoding encoding() {
        return new Base64Encoding();
    }
}
