package com.googlecode.utterlyidle;

import org.junit.Test;

import static com.googlecode.totallylazy.Assert.assertThat;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.Status.ACCEPTED;
import static com.googlecode.utterlyidle.Status.CREATED;
import static com.googlecode.utterlyidle.Status.OK;

public class ResponseTest {
    @Test
    public void supportsOk() throws Exception {
        assertThat(Response.ok().status(), is(OK));
    }

    @Test
    public void supportsCreated() throws Exception {
        Response response = Response.created("/kitten/new");
        assertThat(response.status(), is(CREATED));
        assertThat(response.header(LOCATION).get(), is("/kitten/new"));
    }

    @Test
    public void supportsAccepted() throws Exception {
        assertThat(Response.accepted().status(), is(ACCEPTED));
    }

    @Test
    public void supportsCookiesWithAttributes() throws Exception {

    }

}