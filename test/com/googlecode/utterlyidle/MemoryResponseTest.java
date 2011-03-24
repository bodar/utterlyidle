package com.googlecode.utterlyidle;

import org.junit.Test;

import javax.ws.rs.core.HttpHeaders;

import static com.googlecode.totallylazy.Dates.date;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;

public class MemoryResponseTest {
    @Test
    public void shouldSupportSettingCookieAttributes() throws Exception {
        Response response = response().cookie("a", cookie("1", comment("some comment"), domain(".acme.com"), maxAge(123), path("/products"), secure(), expires(date(2010, 12, 26, 13, 16, 59))));

        assertThat(
                response.header(HttpHeaders.SET_COOKIE),
                is("a=\"1\"; Comment=\"some comment\"; Domain=\".acme.com\"; Max-Age=\"123\"; Path=\"/products\"; Secure=\"\"; Expires=\"Sun, 26-Dec-2010 13:16:59 GMT\""));
    }

    @Test
    public void shouldPrintContent() throws Exception {
        String content = "<blah></blah>";
        Response response = response().header("X-Forwarded-For", "192.168.0.1").bytes(content.getBytes());
        assertThat(response.toString(), endsWith(content));
    }


}
