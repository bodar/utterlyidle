package com.googlecode.utterlyidle.html;

import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class HtmlTest {
    @Test
    public void printsContentIfItCantParseIt() throws Exception {
        try {
            new Html("foo");
            fail("should have thrown");
        } catch (IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("foo"));
        }
    }
}
