package com.googlecode.utterlyidle.io;

import com.googlecode.totallylazy.matchers.NumberMatcher;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HierarchicalPathTest {
    @Test
    public void correctlyJoinsPaths() throws Exception {
        HierarchicalPath path = new HierarchicalPath("");

        assertThat(path.segments().size(), NumberMatcher.is(0));
        assertThat(path.file("bob").toString(), is("bob"));
    }

    @Test
    public void correctlyJoinsPathsThatStartWithASlash() throws Exception {
        HierarchicalPath path = new HierarchicalPath("/");

        assertThat(path.segments().size(), NumberMatcher.is(1));
        assertThat(path.file("bob").toString(), is("/bob"));
    }

}
