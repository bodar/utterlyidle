package com.googlecode.utterlyidle.annotations;

import com.googlecode.utterlyidle.Binding;
import org.junit.Test;

import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static org.hamcrest.MatcherAssert.assertThat;

public class AnnotationsTest {
    @Test
    public void supportsViewAnnotation() throws Exception {
        Binding binding = annotatedClass(UsesViewAnnotation.class)[0];
        assertThat(binding.view(), is(some("foo")));
    }

    public static class UsesViewAnnotation {
        @GET
        @Path("ignored")
        @View("foo")
        public void get() {}

    }
}
