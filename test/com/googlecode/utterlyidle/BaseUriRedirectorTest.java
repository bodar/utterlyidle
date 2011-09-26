package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.dsl.DslTest;
import org.junit.Test;

import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.BaseUri.baseUri;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.get;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.pathParam;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.queryParam;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BaseUriRedirectorTest {
    @Test
    public void extractsUrlFromABinding() throws Exception {
        RegisteredResources bindings = new RegisteredResources();
        bindings.add(get("/redirect").resource(method(on(DslTest.Redirect.class).redirect())).build());

        Redirector redirector = new BaseUriRedirector(baseUri("http://test/path/"), bindings);

        Response response = redirector.redirectTo(method(on(DslTest.Redirect.class).redirect()));
        assertThat(response.status(), is(Status.SEE_OTHER));
        assertThat(response.header(HttpHeaders.LOCATION), is("http://test/path/redirect"));
    }

    @Test
    public void supportsPathParameters() throws Exception {
        RegisteredResources bindings = new RegisteredResources();
        bindings.add(get("/redirect/{foo}").resource(method(on(DslTest.Redirect.class).redirect(pathParam(String.class, "foo")))).build());

        Redirector redirector = new BaseUriRedirector(baseUri("http://test/path/"), bindings);

        Response response = redirector.redirectTo(method(on(DslTest.Redirect.class).redirect("bar")));
        assertThat(response.status(), is(Status.SEE_OTHER));
        assertThat(response.header(HttpHeaders.LOCATION), is("http://test/path/redirect/bar"));
    }

    @Test
    public void supportsQueryParameters() throws Exception {
        RegisteredResources bindings = new RegisteredResources();
        bindings.add(get("/redirect").resource(method(on(DslTest.Redirect.class).redirect(queryParam(String.class, "foo")))).build());

        Redirector redirector = new BaseUriRedirector(baseUri("http://test/path/"), bindings);

        Response response = redirector.redirectTo(method(on(DslTest.Redirect.class).redirect("bar")));
        assertThat(response.status(), is(Status.SEE_OTHER));
        assertThat(response.header(HttpHeaders.LOCATION), is("http://test/path/redirect?foo=bar"));
    }
    
    @Test
    public void canExtractPathWithStreamingWriter() {
        RegisteredResources bindings = new RegisteredResources();
        bindings.add(annotatedClass(SomeResource.class));

        Redirector redirector = new BaseUriRedirector(baseUri("http://server/base/"), bindings);

        Response response = redirector.redirectTo(method(on(SomeResource.class).getStreamingWriter("foo")));
        assertThat(response.status(), is(Status.SEE_OTHER));
        assertThat(response.header(HttpHeaders.LOCATION), is("http://server/base/path/foo"));
    }

}
