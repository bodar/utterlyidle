package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.cookies.Cookie;
import org.junit.Test;

import static com.googlecode.totallylazy.Assert.assertThat;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.totallylazy.time.Dates.date;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.Status.*;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.expires;

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
        Cookie cookie = cookie("name", "Dan", expires(date(2001, 1, 1)));
        Response response = Response.ok().cookie(cookie);
        assertThat(response.cookies().get(cookie.name()), is(some(cookie)));
    }
}