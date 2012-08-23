package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Status;
import org.junit.Test;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.PathMatcher.pathMatches;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.handlers.CompositeHandler.compositeHandler;
import static com.googlecode.utterlyidle.handlers.ReturnResponseHandler.returnsResponse;
import static org.hamcrest.MatcherAssert.assertThat;

public class CompositeHandlerTest {
    @Test
    public void canMatchByPath() throws Exception {
        HttpHandler handler = compositeHandler().add(pathMatches("user"), returnsResponse("Hello"));
        assertThat(handler.handle(get("user").build()).entity().toString(), is("Hello"));
    }

    @Test
    public void ifNoMatchThen404() throws Exception {
        HttpHandler handler = compositeHandler();
        assertThat(handler.handle(get("user").build()).status(), is(Status.NOT_FOUND));
    }
}
