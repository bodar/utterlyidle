package com.googlecode.utterlyidle.annotations;

import com.googlecode.utterlyidle.UriTemplate;
import org.junit.Test;

import java.lang.reflect.Method;

import static com.googlecode.utterlyidle.annotations.UriTemplateExtractor.uriTemplate;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UriTemplateExtractorTest {
    @Test
    public void mergesClassAndMethodPathAnnotations() {
        @Path("/test")
        class TestResource {
            @GET
            @Path("/method")
            public Method method() {
                return new Object() {}.getClass().getEnclosingMethod();
            }
        }

        assertThat(uriTemplate(new TestResource().method()), is(UriTemplate.uriTemplate("test/method")));
    }
}
