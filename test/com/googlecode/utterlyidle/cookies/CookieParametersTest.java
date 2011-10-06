package com.googlecode.utterlyidle.cookies;

import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Requests;
import org.junit.Test;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.cookies.CookieParameters.cookies;
import static com.googlecode.utterlyidle.cookies.CookieParameters.toHttpHeader;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CookieParametersTest {
    @Test
    public void shouldHandleTrailingSpaces() throws Exception {
        CookieParameters cookies = cookies(request(headerParameters(pair("Cookie", "a=1; ; ;"))).headers());

        assertThat(cookies.getValue("a"), is("1"));
    }

    @Test
    public void shouldBeCaseInsensitive() throws Exception {
        CookieParameters lowercaseCookies = cookies(request(headerParameters(pair("cookie", "a=1"))).headers());
        CookieParameters uppercaseCookies = cookies(request(headerParameters(pair("COOKIE", "b=2"))).headers());

        assertThat(lowercaseCookies.getValue("a"), is("1"));
        assertThat(uppercaseCookies.getValue("b"), is("2"));
    }

    @Test
    public void shouldCopeWithRequestCookiesInMultipleHeaders() throws Exception {
        CookieParameters cookies = cookies(request(headerParameters(pair("Cookie", "a=1"), pair("Cookie", "b=2"))).headers());

        assertThat(cookies.getValue("a"), is("1"));
        assertThat(cookies.getValue("b"), is("2"));
    }

    @Test
    public void willIgnoreAttributesOnRequestCookiesForTheTimeBeing() throws Exception {
        CookieParameters cookies = cookies(request(headerParameters(pair("Cookie", "$Version=1; a=1; $Path=whatever; $Domain=something; b=2"))).headers());

        assertThat(cookies.getValue("$Version"), is(nullValue()));
        assertThat(cookies.getValue("$Path"), is(nullValue()));
        assertThat(cookies.getValue("$Domain"), is(nullValue()));

        assertThat(cookies.getValue("a"), is("1"));
        assertThat(cookies.getValue("b"), is("2"));
    }

    @Test
    public void shouldHandleDoubleQuotesInCookieValues() throws Exception {
        assertThat(
                toHttpHeader("a", cookie("Some \"double quoted thing\"")),
                is("a=\"Some \\\"double quoted thing\\\"\"; "));
    }

    @Test
    public void copesWithCookieHeaderWithNoCookiesBoo() {
        CookieParameters cookies = cookies(request(headerParameters(pair("Cookie", ""))).headers());

        assertThat(cookies.size(), is(0));
    }

    private Request someRequest() {
        return request(headerParameters());
    }

    private Request request(HeaderParameters headers) {
        return Requests.request(null, null, headers, null);
    }
}
