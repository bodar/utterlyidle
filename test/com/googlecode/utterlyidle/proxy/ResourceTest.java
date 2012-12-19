package com.googlecode.utterlyidle.proxy;

import com.googlecode.utterlyidle.BaseUriRedirector;
import com.googlecode.utterlyidle.Redirector;
import com.googlecode.utterlyidle.RegisteredResources;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.Path;
import org.junit.Test;

import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.BaseUri.baseUri;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ResourceTest {
    @Test
    public void urlsAreAlwaysAbsolute() throws Exception {
        RegisteredResources bindings = new RegisteredResources();
        bindings.add(annotatedClass(HomePage.class));

        Redirector redirector = new BaseUriRedirector(baseUri("http://test/path/"), bindings);

        assertThat(redirector.uriOf(method(on(HomePage.class).noPath())).toString(), is("http://test/path/"));
        assertThat(redirector.uriOf(method(on(HomePage.class).justSlash())).toString(), is("http://test/path/"));
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
