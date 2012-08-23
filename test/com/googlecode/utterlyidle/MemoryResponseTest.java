package com.googlecode.utterlyidle;

import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.one;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.HttpHeaders.SET_COOKIE;
import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_FOR;
import static com.googlecode.utterlyidle.Response.methods.header;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.comment;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.domain;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.expires;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.maxAge;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.path;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.secure;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;

public class MemoryResponseTest {
    @Test
    public void shouldSupportSettingCookieAttributes() throws Exception {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Australia/Sydney"));
        calendar.set(2011, 8, 4, 16, 15, 36);
        Response response = ResponseBuilder.response().
                cookie(cookie("a", "1", comment("some comment"), domain(".acme.com"), maxAge(123), path("/products"), secure(), expires(calendar.getTime()))).build();

        assertThat(
                header(response, SET_COOKIE),
                is("a=\"1\"; Comment=some comment; Domain=.acme.com; Max-Age=123; Path=/products; Secure=; Expires=Sun, 04 Sep 2011 06:15:36 UTC"));
    }

    @Test
    public void shouldPrintContent() throws Exception {
        String content = "<blah></blah>";
        Response response = response(Status.OK, one(pair(X_FORWARDED_FOR, "192.168.0.1")), content);
        assertThat(response.toString(), endsWith(content));
    }

    @Test
    public void shouldSupportEquals() {
        assertThat(response(OK), is(response(OK)));
    }

    @Test
    public void fieldNamesAreCaseInsensitive() {
        assertThat(response(OK, one(pair("Content-Type", "text/plain"))), is(response(OK, one(pair("content-type", "text/plain")))));
    }

    @Test
    public void fieldValuesAreCaseSensitive() {
        assertThat(response(OK, one(pair("Content-Type", "TEXT/PLAIN"))), is(not(response(OK, one(pair("Content-Type", "text/plain"))))));
    }

    @Test
    public void orderOfHeadersDoesNotMatter() {
        assertThat(response(OK, sequence(pair("name1", "value1"), pair("name2", "value2"))), is(response(OK, sequence(pair("name2", "value2"), pair("name1", "value1")))));
    }
}
