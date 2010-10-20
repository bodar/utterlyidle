package com.googlecode.utterlyidle;

import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ClientErrorTests {
    @Test
    public void shouldReturn404WhenPathNotFound() throws Exception {
        TestEngine engine = new TestEngine();
        Response response = Response.response();
        engine.handle(get("invalidPath"), response);

        assertThat(response.code(), is(Status.NOT_FOUND));
    }

    @Test
    public void shouldReturn405WhenMethodDoesNotMatch() throws Exception {
        TestEngine engine = new TestEngine();
        Response response = Response.response();
        engine.add(Foo.class);
        engine.handle(post("path"), response);

        assertThat(response.code(), is(Status.METHOD_NOT_ALLOWED));
    }

    @Test
    public void shouldReturn415WhenResourceCanNotConsumeType() throws Exception {
        TestEngine engine = new TestEngine();
        Response response = Response.response();
        engine.add(Foo.class);
        engine.handle(get("path").withHeader(HttpHeaders.CONTENT_TYPE, "application/rubbish"), response);

        assertThat(response.code(), is(Status.UNSUPPORTED_MEDIA_TYPE));
    }

    @Test
    public void shouldReturn406WhenAcceptHeaderDoesNotMatch() throws Exception {
        TestEngine engine = new TestEngine();
        Response response = Response.response();
        engine.add(Foo.class);
        engine.handle(get("bob").accepting("application/gibberish"), response);

        assertThat(response.code(), is(Status.NOT_ACCEPTABLE));
    }

    @Test
    public void shouldReturn200WhenAcceptHeaderNotSpecified() throws Exception {
        TestEngine engine = new TestEngine();
        Response response = Response.response();
        engine.add(Foo.class);
        engine.handle(get("bob"), response);

        assertThat(response.code(), is(Status.OK));
    }

    public static class Foo{
        @GET
        @Path("path")
        @Consumes("text/text")
        public void Bar(){

        }

        @GET
        @Path("bob")
        @Produces("text/text")
        public String Bob(){
            return "";
        }
    }

}
