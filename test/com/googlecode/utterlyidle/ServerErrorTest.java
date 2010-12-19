package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.handlers.WriteMessageToResponseHandler;
import com.googlecode.yadic.ContainerException;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.utterlyidle.MemoryResponse.response;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;

public class ServerErrorTest {
    @Test
    public void supportsInterceptingException() throws Exception {
        final String message = "Caught exception";
        TestEngine engine = new TestEngine();
        engine.add(ThrowingResource.class);
        engine.responseHandlers().add(instanceOf(Exception.class), new WriteMessageToResponseHandler(message));
        Response response = response();
        engine.handle(get("exception"), response);
        assertThat(response.output().toString(), containsString(message));
    }

    @Test
    public void returns500WhenAnExceptionIsThrown() throws Exception {
        TestEngine engine = new TestEngine();
        engine.add(ThrowingResource.class);
        Response response = response();
        engine.handle(get("exception"), response);

        assertResponseContains(response, Exception.class);
    }

    @Test
    public void returns500WhenARespurceCanNotBeCreatedByYadic() throws Exception {
        TestEngine engine = new TestEngine();
        engine.add(ResourceWithMissingDependency.class);
        Response response = response();
        engine.handle(get("lazy"), response);

        assertResponseContains(response, ContainerException.class);
    }

    private void assertResponseContains(Response response, final Class<? extends Exception> exceptionClass) {
        assertThat(response.status(), is(Status.INTERNAL_SERVER_ERROR));
        assertThat(response.output().toString(), containsString(exceptionClass.getName()));
    }

    public static class ThrowingResource {
        @GET
        @Path("exception")
        public String get() throws Exception {
            throw new Exception();
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
