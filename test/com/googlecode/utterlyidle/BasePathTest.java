package com.googlecode.utterlyidle;

import org.junit.Test;

import static com.googlecode.utterlyidle.BasePath.basePath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class BasePathTest {
    @Test
    public void shouldAlwaysEndInSlash() throws Exception {
        assertThat(basePath(""), is(equalTo(basePath("/"))));
    }

}
