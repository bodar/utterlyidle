package com.googlecode.utterlyidle.cookies;

import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import java.util.Date;

import static com.googlecode.totallylazy.Callables.second;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.matchers.IterableMatcher.hasExactly;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.comment;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.domain;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.expires;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.maxAge;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.path;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.secure;
import static com.googlecode.utterlyidle.cookies.CookieName.cookieName;
import static com.googlecode.utterlyidle.cookies.Cookies.REQUEST_COOKIE_HEADER;
import static com.googlecode.utterlyidle.cookies.Cookies.SET_COOKIE_HEADER;
import static com.googlecode.utterlyidle.cookies.Cookies.cookies;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CookiesTest {
    private Response response = new Response();

    @Test
    public void shouldHandleTrailingSpaces() throws Exception {
        Cookies cookies = cookies(request(headerParameters(pair("Cookie", "a=1; ; ;"))), null);

        assertThat(cookies.getValue(cookieName("a")), is("1"));
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

        assertThat(response.headers().getValue(SET_COOKIE_HEADER), is(nullValue()));

        cookies.commit();

        assertThat(
                sequence(response.headers()).sortBy(second(String.class)),
                hasExactly(
                        pair(SET_COOKIE_HEADER, "a=\"1\"; "),
                        pair(SET_COOKIE_HEADER, "b=\"2\"; ")));
    }

    @Test
    public void shouldAllowRollbackOfChanges() throws Exception {
        Cookies cookies = cookies(someRequest(), response);
        cookies.set(cookieName("a"), "1");

        cookies.rollback();
        cookies.commit();

        assertThat(response.headers().getValue(SET_COOKIE_HEADER), is(nullValue()));
    }

    @Test
    public void shouldSupportSettingCookieAttributes() throws Exception {
        Cookies cookies = cookies(someRequest(), response);
        cookies.set(cookie(cookieName("a"), "1", comment("some comment"), domain(".acme.com"), maxAge(123), path("/products"), secure(), expires(new Date(77, 7, 30, 9, 32, 59))));

        assertThat(response.headers().getValue(SET_COOKIE_HEADER), is(nullValue()));

        cookies.commit();

        assertThat(
                response.headers().getValue(SET_COOKIE_HEADER),
                is("a=\"1\"; Comment=\"some comment\"; Domain=\".acme.com\"; Max-Age=\"123\"; Path=\"/products\"; Secure=\"\"; Expires=\"Tue, 30-Aug-1977 09:32:59 GMT\""));
    }

    @Test
    public void shouldCorrectlyReadAndWrite() throws Exception {
        Cookies cookies = cookies(someRequest(), response);
        final CookieName cookieName = cookieName("a");
        cookies.set(cookie(cookieName, "1"));
        cookies.commit();

        final String value = response.headers().getValue(SET_COOKIE_HEADER);
        final HeaderParameters headers = (HeaderParameters) headerParameters().add(REQUEST_COOKIE_HEADER, value);
        Cookies cookiesRead = cookies(request(headers), response);
        assertThat(cookiesRead.getValue(cookieName), is("1"));
    }

    private Request someRequest() {
        return request(headerParameters());
    }
    private Request request(HeaderParameters headers) {
        return Request.request(null, null, headers, null);
    }
}
