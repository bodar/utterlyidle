package com.googlecode.utterlyidle.cookies;

import com.googlecode.utterlyidle.HeaderParameters;
import com.googlecode.utterlyidle.ParametersContract;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Requests;
import com.googlecode.utterlyidle.annotations.HttpMethod;
import org.junit.Test;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.one;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.cookies.CookieParameters.cookies;
import static com.googlecode.utterlyidle.cookies.CookieParameters.toHttpHeader;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CookieParametersTest extends ParametersContract<CookieParameters> {
    @Override
    protected CookieParameters parameters() {
        return cookies(headerParameters());
    }

    @Test
    public void shouldHandleTrailingSpaces() throws Exception {
        CookieParameters cookies = cookies(request(headerParameters(one(pair("Cookie", "a=1; ; ;")))).headers());

        assertThat(cookies.getValue("a"), is("1"));
    }

    @Test
    public void shouldBeCaseInsensitive() throws Exception {
        CookieParameters lowercaseCookies = cookies(request(headerParameters(one(pair("cookie", "a=1")))).headers());
        CookieParameters uppercaseCookies = cookies(request(headerParameters(one(pair("COOKIE", "b=2")))).headers());

        assertThat(lowercaseCookies.getValue("a"), is("1"));
        assertThat(uppercaseCookies.getValue("b"), is("2"));
    }

    @Test
    public void shouldCopeWithRequestCookiesInMultipleHeaders() throws Exception {
        CookieParameters cookies = cookies(request(headerParameters(sequence(pair("Cookie", "a=1"), pair("Cookie", "b=2")))).headers());

        assertThat(cookies.getValue("a"), is("1"));
        assertThat(cookies.getValue("b"), is("2"));
    }

    @Test
    public void willIgnoreAttributesOnRequestCookiesForTheTimeBeing() throws Exception {
        CookieParameters cookies = cookies(request(headerParameters(one(pair("Cookie", "$Version=1; a=1; $Path=whatever; $Domain=something; b=2")))).headers());

        assertThat(cookies.getValue("$Version"), is(nullValue()));
        assertThat(cookies.getValue("$Path"), is(nullValue()));
        assertThat(cookies.getValue("$Domain"), is(nullValue()));

        assertThat(cookies.getValue("a"), is("1"));
        assertThat(cookies.getValue("b"), is("2"));
    }

    @Test
    public void copesWithCookieHeaderWithNoCookies() {
        CookieParameters cookies = cookies(request(headerParameters(one(pair("Cookie", "")))).headers());
        assertThat(cookies.size(), is(0));
    }

    private Request request(HeaderParameters headers) {
        return Requests.request(HttpMethod.GET, null, headers, null);
    }
}