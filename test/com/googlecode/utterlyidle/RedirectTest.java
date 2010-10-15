package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.servlet.BasePath;
import org.junit.Test;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;

import static com.googlecode.utterlyidle.Redirect.redirect;
import static com.googlecode.utterlyidle.Redirect.resource;
import static com.googlecode.utterlyidle.Response.response;
import static com.googlecode.utterlyidle.servlet.BasePath.basePath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RedirectTest {
    @Test
    public void canExtractPath() {
        assertThat(redirect(resource(SomeResource.class).getHtml("foo")).location(), is("path/foo"));
    }

    @Test
    public void canExtractPathWithStreamingOutput() throws IOException {
        assertThat(redirect(resource(SomeResource.class).getStreamingHtml("foo")).location(), is("path/foo"));
    }


    @Test
    public void canExtractPathWithStreamingWriter() {
        assertThat(redirect(resource(SomeResource.class).getStreamingWriter("foo")).location(), is("path/foo"));
    }

    @Test
    public void canHandleClassWithNoDefaultConstructor() throws IOException {
        assertThat(redirect(resource(NoDefaultConstructor.class).getStreamingHtml("foo")).location(), is("path/foo"));
    }

    @Test
    public void canHandleCustomTypeWithSimpleToString() {
        Id id = Id.id("foo");
        assertThat(redirect(resource(CustomType.class).getHtml(id)).location(), is("path/" + id.toString()));
    }

    @Test
    public void canApplyToResponse() {
        Response response = response();
        BasePath base = basePath("");
        redirect("foo").applyTo(base, response);
        assertThat(response.headers().getValue(HttpHeaders.LOCATION), is("/foo"));
        assertThat(response.code(), is(Status.SEE_OTHER));
    }

    @Path("path/{id}")
    static class SomeResource {
        public String getHtml(@PathParam("id") String id) {
            return "bob";
        }

        public StreamingOutput getStreamingHtml(@PathParam("id") String id) {
            return null;
        }

        public StreamingWriter getStreamingWriter(@PathParam("id") String id) {
            return null;
        }
    }

    @Path("path/{id}")
    static class NoDefaultConstructor {
        NoDefaultConstructor(SomeResource someResource) {
        }

        public String getHtml(@PathParam("id") String id) {
            return "bob";
        }

        public StreamingOutput getStreamingHtml(@PathParam("id") String id) {
            return null;
        }
    }

    public static class Id {
        private final String value;

        private Id(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static Id id(String value) {
            return new Id(value);
        }
    }

    @Path("path/{id}")
    static class CustomType {
        public String getHtml(@PathParam("id") Id id) {
            return "bob";
        }

    }
}