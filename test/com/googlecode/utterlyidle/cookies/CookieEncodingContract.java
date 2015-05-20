package com.googlecode.utterlyidle.cookies;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public abstract class CookieEncodingContract {

    @Test
    public void encodesValue() throws Exception {
        String encoded = encoding().encode(input());
        assertThat(encoded, is(expectedOutput()));
    }

    @Test
    public void codecIsSymmetrical() throws Exception {
        assertThat(encoding().decode(encoding().encode(input())), is(input()));
    }

    protected abstract String input();
    protected abstract String expectedOutput();
    protected abstract CookieEncoding encoding();
}