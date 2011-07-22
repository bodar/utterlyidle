package com.googlecode.utterlyidle;

import org.junit.Test;

import com.googlecode.utterlyidle.annotations.Consumes;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.Produces;
import com.googlecode.utterlyidle.annotations.QueryParam;

import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class ClientErrorTest {
    @Test
    public void shouldReturn404WhenPathNotFound() throws Exception {
        Response response = application().handle(get("invalidPath"));
        assertThat(response.status(), is(Status.NOT_FOUND));
    }

    @Test
    public void shouldReturn405WhenMethodDoesNotMatch() throws Exception {
        TestApplication application = new TestApplication();
        application.add(Foo.class);
        Response response = application.handle(post("path"));

        assertThat(response.status(), is(Status.METHOD_NOT_ALLOWED));
    }

    @Test
    public void shouldReturn400WhenMethodMatchesButARequiredArgumentIsMissing() throws Exception {
        TestApplication application = new TestApplication();
        application.add(SomeOther.class);
        Response response = application.handle(get("path").withQuery("someOther", "value"));

        assertThat(response.status(), is(Status.UNSATISFIABLE_PARAMETERS));
    }

    @Test
    public void shouldNotMatchArgumentsThatAreOfTypeObject() throws Exception {
        TestApplication application = new TestApplication();
        application.add(SomeOther.class);
        Response response = application.handle(get("object"));

        assertThat(response.status(), is(Status.UNSATISFIABLE_PARAMETERS));
    }

    @Test
    public void shouldReturn415WhenResourceCanNotConsumeType() throws Exception {
        TestApplication application = new TestApplication();
        application.add(Foo.class);
        Response response = application.handle(get("path").withHeader(HttpHeaders.CONTENT_TYPE, "application/rubbish"));

        assertThat(response.status(), is(Status.UNSUPPORTED_MEDIA_TYPE));
    }

    @Test
    public void shouldReturn406WhenAcceptHeaderDoesNotMatch() throws Exception {
        TestApplication application = new TestApplication();
        application.add(Foo.class);
        Response response = application.handle(get("bob").accepting("application/gibberish"));

        assertThat(response.status(), is(Status.NOT_ACCEPTABLE));
    }

    @Test
    public void shouldReturn200WhenAcceptHeaderNotSpecified() throws Exception {
        TestApplication application = new TestApplication();
        application.add(Foo.class);
        Response response = application.handle(get("bob"));

        assertThat(response.status(), is(Status.OK));
    }

    public static class SomeOther{
        @GET
        @Path("path")
        public void method(@QueryParam("directoryNumber") String o){
            fail("Should never get here");
        }

        @GET
        @Path("object")
        public void method(@QueryParam("evil") Object o){
            fail("Should never get here");
        }

    }

    public static class Foo{
        @GET
        @Path("path")
        @Consumes("text/text")
        public void Bar(@QueryParam("directoryNumber") String o){

        }

        @GET
        @Path("bob")
        @Produces("text/text")
        public String Bob(){
            return "";
        }
    }

}
