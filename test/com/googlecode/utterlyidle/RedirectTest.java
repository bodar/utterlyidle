package com.googlecode.utterlyidle;

import org.junit.Test;

import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.SeeOther.seeOther;
import static com.googlecode.utterlyidle.Response.response;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RedirectTest {
    @Test
    public void canApplyToResponse() {
        Response response = response();
        BasePath base = basePath("");
        seeOther("foo").applyTo(base, response);
        assertThat(response.headers().getValue(HttpHeaders.LOCATION), is("/foo"));
        assertThat(response.code(), is(Status.SEE_OTHER));
    }

}