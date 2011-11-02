package com.googlecode.utterlyidle;

import org.junit.Test;

import static com.googlecode.utterlyidle.RequestBuilder.post;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MethodMatcherTest {
    @Test
    public void matchesIsCaseInsensitive() throws Exception {
        assertThat(new MethodMatcher("post").matches(post("/").build()), is(true));
    }
}
