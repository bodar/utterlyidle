package com.googlecode.utterlyidle.rendering;

import com.googlecode.totallylazy.Sequences;
import org.junit.Test;

import static com.googlecode.totallylazy.matchers.IterableMatcher.hasExactly;
import static com.googlecode.utterlyidle.rendering.Model.model;
import static org.hamcrest.MatcherAssert.assertThat;

public class ModelTest {
    @Test
    public void canAddSequences() throws Exception {
        final Model model = model().add("bob", Sequences.sequence("foo", "bar"));
        assertThat(model.get("bob"), hasExactly((Object)"foo", "bar"));
    }
}
