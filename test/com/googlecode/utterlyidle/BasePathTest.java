package com.googlecode.utterlyidle;

import org.junit.Test;

import static com.googlecode.utterlyidle.BasePath.basePath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class BasePathTest {
    @Test
    public void isAbsoluteFolder() throws Exception {
        assertThat(basePath("foo"), is(equalTo(basePath("/foo/"))));
        assertThat(basePath("/foo/"), is(equalTo(basePath("/foo/"))));
        assertThat(basePath("/foo"), is(equalTo(basePath("/foo/"))));
        assertThat(basePath("foo/"), is(equalTo(basePath("/foo/"))));
        assertThat(basePath("foo/bar"), is(equalTo(basePath("/foo/bar/"))));
        assertThat(basePath("/foo/bar"), is(equalTo(basePath("/foo/bar/"))));
        assertThat(basePath("foo/bar"), is(equalTo(basePath("/foo/bar/"))));
        assertThat(basePath("foo/bar/"), is(equalTo(basePath("/foo/bar/"))));
    }

}
