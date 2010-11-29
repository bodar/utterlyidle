package com.googlecode.utterlyidle;

import org.junit.Test;

import java.io.ByteArrayInputStream;

import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.RequestBuilder.cloneOf;
import static com.googlecode.utterlyidle.io.Url.url;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RequestBuilderTest {
    @Test
    public void shouldBeAbleToCloneAnExistingRequest() {
        Request original = new Request("GET", url("http://google.com?q=moomin"), headerParameters(), new ByteArrayInputStream(new byte[0]), basePath("/somewhere"));
        Request clone = cloneOf(original).build();
        
        assertThat(clone.toString(), is(original.toString()));
    }
}
