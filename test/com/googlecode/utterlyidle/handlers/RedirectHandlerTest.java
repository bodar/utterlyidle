package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.core.HttpHeaders;

import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.MemoryResponse.response;
import static com.googlecode.utterlyidle.SeeOther.seeOther;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RedirectHandlerTest {
    @Test
    public void canApplyToResponse() {
        Response response = response();
        BasePath base = basePath("");
        new RedirectHandler(base).handle(null, response.entity(seeOther("foo")));
        assertThat(response.header(HttpHeaders.LOCATION), is("/foo"));
        MatcherAssert.assertThat(response.status(), Matchers.is(Status.SEE_OTHER));
    }
}