package com.googlecode.utterlyidle.cookies;

import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.MemoryResponse;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Requests;
import org.junit.Test;

import static com.googlecode.totallylazy.Dates.date;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.*;
import static com.googlecode.utterlyidle.cookies.CookieName.cookieName;
import static com.googlecode.utterlyidle.cookies.Cookies.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CookiesTest {
    private MemoryResponse response = new MemoryResponse();

    @Test
    public void shouldHandleTrailingSpaces() throws Exception {
        Cookies cookies = cookies(request(headerParameters(pair("Cookie", "a=1; ; ;"))), null);

        assertThat(cookies.getValue(cookieName("a")), is("1"));
    }

    @Test
    public void shouldBeCaseInsensitive() throws Exception {
        Cookies lowercaseCookies = cookies(request(headerParameters(pair("cookie", "a=1"))), null);
        Cookies uppercaseCookies = cookies(request(headerParameters(pair("COOKIE", "b=2"))), null);

        assertThat(lowercaseCookies.getValue(cookieName("a")), is("1"));
        assertThat(uppercaseCookies.getValue(cookieName("b")), is("2"));
    }

    @Test
    public void shouldCopeWithRequestCookiesInMultipleHeaders() throws Exception {
        Cookies cookies = cookies(request(headerParameters(pair("Cookie", "a=1"), pair("Cookie", "b=2"))), null);

        assertThat(cookies.getValue(cookieName("a")), is("1"));
        assertThat(cookies.getValue(cookieName("b")), is("2"));
    }

    @Test
    public void willIgnoreAttributesOnRequestCookiesForTheTimeBeing() throws Exception {
        Cookies cookies = cookies(request(headerParameters(pair("Cookie", "$Version=1; a=1; $Path=whatever; $Domain=something; b=2"))), null);

        assertThat(cookies.getValue(cookieName("$Version")), is(nullValue()));
        assertThat(cookies.getValue(cookieName("$Path")), is(nullValue()));
        assertThat(cookies.getValue(cookieName("$Domain")), is(nullValue()));

        assertThat(cookies.getValue(cookieName("a")), is("1"));
        assertThat(cookies.getValue(cookieName("b")), is("2"));
    }

    @Test
    public void shouldCommitCookiesToResponse() throws Exception {
        Cookies cookies = cookies(someRequest(), response);
        cookies.set(cookieName("a"), "1");
        cookies.set(cookieName("b"), "2");

        assertThat(response.header(SET_COOKIE_HEADER), is(nullValue()));

        cookies.commit();

        assertThat(response.headers(SET_COOKIE_HEADER), containsInAnyOrder("a=\"1\"; ", "b=\"2\"; "));
    }

    @Test
    public void shouldAllowRollbackOfChanges() throws Exception {
        Cookies cookies = cookies(someRequest(), response);
        cookies.set(cookieName("a"), "1");

        cookies.rollback();
        cookies.commit();

        assertThat(response.header(SET_COOKIE_HEADER), is(nullValue()));
    }

    @Test
    public void shouldSupportSettingCookieAttributes() throws Exception {
        Cookies cookies = cookies(someRequest(), response);
        cookies.set(cookie(cookieName("a"), "1", comment("some comment"), domain(".acme.com"), maxAge(123), path("/products"), secure(), expires(date(2010, 12, 26, 13, 16, 59))));

        assertThat(response.header(SET_COOKIE_HEADER), is(nullValue()));

        cookies.commit();

        assertThat(
                response.header(SET_COOKIE_HEADER),
                is("a=\"1\"; Comment=\"some comment\"; Domain=\".acme.com\"; Max-Age=\"123\"; Path=\"/products\"; Secure=\"\"; Expires=\"Sun, 26-Dec-2010 13:16:59 GMT\""));
    }

    @Test
    public void shouldCorrectlyReadAndWrite() throws Exception {
        Cookies cookies = cookies(someRequest(), response);
        final CookieName cookieName = cookieName("a");
        cookies.set(cookie(cookieName, "1"));
        cookies.commit();

        final String value = response.header(SET_COOKIE_HEADER);
        final HeaderParameters headers = (HeaderParameters) headerParameters().add(REQUEST_COOKIE_HEADER, value);
        Cookies cookiesRead = cookies(request(headers), response);
        assertThat(cookiesRead.getValue(cookieName), is("1"));
    }

    private Request someRequest() {
        return request(headerParameters());
    }

    private Request request(HeaderParameters headers) {
        return Requests.request(null, null, headers, null);
    }
}
