package com.googlecode.utterlyidle;

import org.junit.Test;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.Cookies.cookies;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CookiesTest {
    private Response response = new Response();

    @Test
    public void shouldParseMultipleRequestCookiesInSameHeader() throws Exception {
        Cookies cookies = cookies(request(headerParameters(pair("Cookie", "a=1; b=2 "))), null);

        assertThat(cookies.getValue("a"), is("1"));
        assertThat(cookies.getValue("b"), is("2 "));
    }

    @Test
    public void willIgnoreAttributesOnRequestCookiesForTheTimeBeing() throws Exception {
        Cookies cookies = cookies(request(headerParameters(pair("Cookie", "$Version=1; a=1; $Path=whatever; $Domain=something; b=2"))), null);

        assertThat(cookies.getValue("$Version"), is(nullValue()));
        assertThat(cookies.getValue("$Path"), is(nullValue()));
        assertThat(cookies.getValue("$Domain"), is(nullValue()));

        assertThat(cookies.getValue("a"), is("1"));
        assertThat(cookies.getValue("b"), is("2"));
    }

    @Test
    public void shouldCommitCookiesToResponse() throws Exception {
        Cookies cookies = cookies(request(), response);
        cookies.set("a", "1");

        assertThat(response.headers().getValue("Set-Cookie"), is(nullValue()));

        cookies.commit();

        assertThat(response.headers().getValue("Set-Cookie"), is("a=1;"));
    }

    @Test
    public void shouldAllowRollbackOfChanges() throws Exception {
        Cookies cookies = cookies(request(), response);
        cookies.set("a", "1");

        cookies.rollback();
        cookies.commit();
        assertThat(response.headers().getValue("Set-Cookie"), is(nullValue()));
    }

    private Request request() {
        return request(headerParameters());
    }
    private Request request(HeaderParameters headers) {
        return new Request(null, null, headers, null, null);
    }
}
