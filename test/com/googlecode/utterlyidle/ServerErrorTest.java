package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.LazyException;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.yadic.Resolver;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

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
    public void returns500WhenARespurceCanNotBeCreatedByYadic() throws Exception {
        TestEngine engine = new TestEngine();
        engine.add(ResourceWithMissingDependency.class);
        Response response = Response.response();
        engine.handle(get("lazy"), response);

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

    public static class ResourceWithMissingDependency {
        public ResourceWithMissingDependency(ThrowingResource missing) {
        }

        @GET
        @Path("lazy")
        public void getLazy() {
            fail("should never get here");
        }
    }
}
