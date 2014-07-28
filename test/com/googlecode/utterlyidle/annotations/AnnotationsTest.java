package com.googlecode.utterlyidle.annotations;

import com.googlecode.utterlyidle.Binding;
import org.junit.Test;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static com.googlecode.utterlyidle.annotations.View.constructors.view;
import static org.hamcrest.MatcherAssert.assertThat;

public class AnnotationsTest {
    @Test
    public void supportsViewAnnotation() throws Exception {
        Binding binding = annotatedClass(Explicit.class)[0];
        assertThat(binding.view(), is(view("foo")));
    }

    @Path("ignored")
    public static class Explicit {
        @GET
        @View("foo")
        public void get() {
        }
    }

    @Test
    public void defaultsViewToMethodNameWhenNoViewAnnotationPresent() throws Exception {
        Binding binding = annotatedClass(Implicit.class)[0];
        assertThat(binding.view(), is(view("get")));
    }

    @Path("ignored")
    public static class Implicit {
        @GET
        public void get() {
        }

    }
}
