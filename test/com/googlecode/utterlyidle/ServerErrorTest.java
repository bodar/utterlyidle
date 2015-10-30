package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.handlers.WriteMessageToResponseHandler;
import com.googlecode.yadic.ContainerException;
import org.junit.Test;

import java.io.IOException;

import static com.googlecode.totallylazy.predicates.Predicates.*;
import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.Request.Builder.get;
import static com.googlecode.utterlyidle.handlers.HandlerRule.entity;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;

public class ServerErrorTest {
    @Test
    public void supportsInterceptingRuntimeException() throws Exception {
        final String message = "Caught exception";
        ApplicationBuilder application = application().addAnnotated(ThrowingRuntimeResource.class);
        application.addResponseHandler(where(entity(), instanceOf(IllegalArgumentException.class)), new WriteMessageToResponseHandler(message));

        Response response = application.handle(get("exception"));

        assertThat(response.entity().toString(), containsString(message));
    }

    @Test
    public void supportsInterceptingCheckedException() throws Exception {
        final String message = "Caught exception";
        ApplicationBuilder application = application().addAnnotated(ThrowingCheckedResource.class);
        application.addResponseHandler(where(entity(), instanceOf(IOException.class)), new WriteMessageToResponseHandler(message));

        Response response = application.handle(get("exception"));

        assertThat(response.entity().toString(), containsString(message));
    }

    @Test
    public void returns500WhenAnExceptionIsThrown() throws Exception {
        ApplicationBuilder application = application().addAnnotated(ThrowingRuntimeResource.class);

        Response response = application.handle(get("exception"));

        assertResponseContains(response, IllegalArgumentException.class);
    }

    @Test
    public void returns500WhenAResourceCanNotBeCreatedByYadic() throws Exception {
        ApplicationBuilder application = application().addAnnotated(ResourceWithMissingDependency.class);

        Response response = application.handle(get("lazy"));

        assertResponseContains(response, ContainerException.class);
    }

    @Test
    public void shouldReturn500whenARendererThrowsException() throws Exception {
        ApplicationBuilder application = application().addAnnotated(NoProblemsResource.class);
        application.addResponseHandler(always(), throwOnRender(new RuntimeException("Boom")));
        Response response = application.handle(get("noProblems"));
        assertResponseContains(response, RuntimeException.class);
    }

    @Test
    public void shouldReturn500whenARendererThrowsError() throws Exception {
        ApplicationBuilder application = application().addAnnotated(NoProblemsResource.class);
        application.addResponseHandler(always(), throwError(new AssertionError()));
        Response response = application.handle(get("noProblems"));
        assertResponseContains(response, AssertionError.class);
    }

     @Test
    public void shouldReturn500whenAResponseMatcherThrowsException() throws Exception {
        ApplicationBuilder application = application().addAnnotated(NoProblemsResource.class);
        application.addResponseHandler(alwaysThrows(), doNothingRenderer());
        Response response = application.handle(get("noProblems"));
        assertResponseContains(response, RuntimeException.class);
    }

    private Predicate<Pair<Request, Response>> alwaysThrows() {
        return requestResponsePair -> {
            throw new RuntimeException();
        };
    }

    private ResponseHandler doNothingRenderer() {
        return response -> response;
    }

    private ResponseHandler throwOnRender(final RuntimeException exception) {
        return response -> {
            throw exception;
        };
    }

    private ResponseHandler throwError(final Error error) {
        return response -> {
            throw error;
        };
    }

    private void assertResponseContains(Response response, final Class<? extends Throwable> exceptionClass) {
        assertThat(response.status(), is(Status.INTERNAL_SERVER_ERROR));
        assertThat(response.entity().toString(), containsString(exceptionClass.getName()));
    }

    public static class ThrowingRuntimeResource {
        @GET
        @Path("exception")
        public String get() throws Exception {
            throw new IllegalArgumentException();
        }
    }

    public static class ThrowingCheckedResource {
        @GET
        @Path("exception")
        public String get() throws Exception {
            throw new IOException();
        }
    }

    public static class ResourceWithMissingDependency {
        public ResourceWithMissingDependency(ThrowingRuntimeResource missing) {
        }

        @GET
        @Path("lazy")
        public void getLazy() {
            fail("should never get here");
        }
    }

    public static class NoProblemsResource {
        @GET
        @Path("noProblems")
        public String get() throws Exception {
            return "noProblems";
        }

    }

}
