package com.googlecode.utterlyidle.cookies;

import org.junit.Test;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.cookies.CookieEncoder.cookieEncoder;
import static org.junit.Assert.assertThat;

public class CookieEncoderTest {

    private final Cookie cookie = cookie("cookie1", "Hob Nob");
    private final CookieEncoder cookieEncoder = cookieEncoder(new EvilEncoding());

    @Test
    public void returnsOriginalCookieIfEncodingFails() throws Exception {
        assertThat(cookieEncoder.encode(cookie), is(cookie));
    }

    @Test
    public void returnsOriginalCookieIfDecodingFails() throws Exception {
        assertThat(cookieEncoder.decode(cookie), is(cookie));
    }

    private static class EvilEncoding implements CookieEncoding {
        @Override
        public String decode(final String input) {
            throw new IllegalArgumentException("Muhahahahaha!");
        }

        @Override
        public String encode(final String input) {
            throw new IllegalArgumentException("Muhahahahaha!");
        }
    }
}