package com.googlecode.utterlyidle.flash;

import org.junit.Test;

import static com.googlecode.totallylazy.matchers.IterableMatcher.hasExactly;
import static org.hamcrest.MatcherAssert.assertThat;

public class FlashTest {
    @Test
    public void supportsErrorsAsAListOfStrings() {
        Flash flash = new Flash().
                error("First error").
                error("Second error").
                error("Third error");

        assertThat(flash.errors(), hasExactly("First error", "Second error", "Third error"));
    }

    @Test
    public void supportsMessagesAsAListOfStrings() {
        Flash flash = new Flash().
                message("First message").
                message("Second message").
                message("Third message");

        assertThat(flash.messages(), hasExactly("First message", "Second message", "Third message"));
    }
}
