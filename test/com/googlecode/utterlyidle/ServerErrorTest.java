package com.googlecode.utterlyidle;

import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ServerErrorTest {
    @Test
    public void returns500WhenAnExceptionIsThrown() throws Exception {
        TestEngine engine = new TestEngine();
        engine.add(ThrowingResource.class);
        Response response = Response.response();
        engine.handle(get("exception"), response);

        assertThat(response.code(), is(Status.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void returns501WhenAnUnsupportedOperationExceptionIsThrown() throws Exception {
        TestEngine engine = new TestEngine();
        engine.add(ThrowingResource.class);
        Response response = Response.response();
        engine.handle(get("not_implemented"), response);

        assertThat(response.code(), is(Status.NOT_IMPLEMENTED));
    }

    public static class ThrowingResource {
        @GET
        @Path("exception")
        public String get() throws Exception {
            throw new Exception();
        }

        @GET
        @Path("not_implemented")
        public String getError() {
            throw new UnsupportedOperationException();
        }
    }
}
