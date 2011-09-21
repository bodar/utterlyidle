package com.googlecode.utterlyidle.proxy;

import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.Path;
import org.junit.Test;

import static com.googlecode.utterlyidle.proxy.Resource.resource;
import static com.googlecode.utterlyidle.proxy.Resource.urlOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ResourceTest {
    @Test
    public void urlsAreAlwaysAbsolute() throws Exception {
        assertThat(urlOf(resource(HomePage.class).noPath()), is("/"));
        assertThat(urlOf(resource(HomePage.class).justSlash()), is("/"));
    }

    public static class HomePage {
        @GET
        @Path("")
        public String noPath() {
            return null;
        }

        @GET
        @Path("/")
        public String justSlash() {
            return null;
        }
    }
}
