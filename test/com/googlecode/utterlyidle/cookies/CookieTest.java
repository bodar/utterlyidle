package com.googlecode.utterlyidle.cookies;

import org.junit.Test;

import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CookieTest {

    @Test
    public void shouldHandleDoubleQuotesInCookieValues() throws Exception {
        assertThat(
            cookie("a", "Some \"double quoted thing\"").toString(),
            is("a=\"Some \\\"double quoted thing\\\"\"; ")
        );
    }
}
