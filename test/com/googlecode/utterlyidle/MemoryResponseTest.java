package com.googlecode.utterlyidle;

import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_FOR;
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
        Response response = response().cookie("a", cookie("1", comment("some comment"), domain(".acme.com"), maxAge(123), path("/products"), secure(), expires(calendar.getTime())));;

        assertThat(
                response.header(HttpHeaders.SET_COOKIE),
                is("a=\"1\"; Comment=\"some comment\"; Domain=\".acme.com\"; Max-Age=\"123\"; Path=\"/products\"; Secure=\"\"; Expires=\"Sun, 04-Sep-2011 06:15:36 GMT\""));
    }

    @Test
    public void shouldPrintContent() throws Exception {
        String content = "<blah></blah>";
        Response response = response().header(X_FORWARDED_FOR, "192.168.0.1").bytes(content.getBytes());
        assertThat(response.toString(), endsWith(content));
    }

    @Test
    public void shouldSupportEquals() {
        assertThat(response().status(OK), is(response().status(OK)));
    }

    @Test
    public void fieldNamesAreCaseInsensitive() {
        assertThat(response().status(OK).header("Content-Type", "text/plain"), is(response().status(OK).header("content-type", "text/plain")));
    }

    @Test
    public void fieldValuesAreCaseSensitive() {
        assertThat(response().status(OK).header("Content-Type", "TEXT/PLAIN"), is(not(response().status(OK).header("Content-Type", "text/plain"))));
    }

    @Test
    public void orderOfHeadersDoesNotMatter() {
        assertThat(response().status(OK).header("name1", "value1").header("name2", "value2"), is(response().status(OK).header("name2", "value2").header("name1", "value1")));
    }
    
}
