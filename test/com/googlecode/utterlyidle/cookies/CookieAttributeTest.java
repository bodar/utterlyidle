package com.googlecode.utterlyidle.cookies;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CookieAttributeTest {
    @Test
    public void shouldTurnValuesIntoQuotedStrings() {
        assertThat(
                new CookieAttribute("somename", "some \"quoted value\"").toString(),
                is("somename=\"some \\\"quoted value\\\"\""));
    }
}
