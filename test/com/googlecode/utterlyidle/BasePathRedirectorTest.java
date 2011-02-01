package com.googlecode.utterlyidle;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.core.HttpHeaders;

import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.Redirect.redirect;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BasePathRedirectorTest {
    @Test
    public void canApplyToResponse() {
        BasePath base = basePath("");
        Response response = new BasePathRedirector(base).redirect(redirect("foo"));
        assertThat(response.header(HttpHeaders.LOCATION), is("/foo"));
        MatcherAssert.assertThat(response.status(), Matchers.is(Status.SEE_OTHER));
    }
}
