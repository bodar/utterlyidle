package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.annotations.*;
import org.junit.Test;

import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.BaseUriRedirectorTest.bindings;
import static com.googlecode.utterlyidle.BaseUriRedirectorTest.redirector;
import static com.googlecode.utterlyidle.Request.Builder.*;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BindingsRequestGeneratorTest {
    @Test
    public void canGenerateGETRequest() throws Exception {
        RequestGenerator requestGenerator = requestGenerator(annotatedClass(RequestGeneratorResource.class));
        Request request = requestGenerator.requestFor(method(on(RequestGeneratorResource.class).get("query", "path")));
        assertThat(request, is(get("http://server/base/test/path?query=query")));
    }

    @Test
    public void canGeneratePOSTRequest() throws Exception {
        RequestGenerator requestGenerator = requestGenerator(annotatedClass(RequestGeneratorResource.class));
        Request request = requestGenerator.requestFor(method(on(RequestGeneratorResource.class).post("query", "form")));
        Request expected = post("http://server/base/test?query=query", form("form", "form"));
        assertThat(request, is(expected));
    }

    private BindingsRequestGenerator requestGenerator(Binding... bindings) {
        return new BindingsRequestGenerator(redirector(bindings), bindings(bindings));
    }

    class RequestGeneratorResource {
        @GET
        @Path("test/{path}")
        public String get(@QueryParam("query") String query, @PathParam("path") String path) {
            return query + path;
        }

        @POST
        @Path("test")
        public String post(@QueryParam("query") String query, @FormParam("form") String form) {
            return query + form;
        }
    }
}
