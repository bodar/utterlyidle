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
import static com.googlecode.utterlyidle.cookies.CookieParameters.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CookieParametersTest {
    private MemoryResponse response = new MemoryResponse();

    @Test
    public void shouldHandleTrailingSpaces() throws Exception {
        CookieParameters cookies = cookies(request(headerParameters(pair("Cookie", "a=1; ; ;"))));

        assertThat(cookies.getValue("a"), is("1"));
    }

    @Test
    public void shouldBeCaseInsensitive() throws Exception {
        CookieParameters lowercaseCookies = cookies(request(headerParameters(pair("cookie", "a=1"))));
        CookieParameters uppercaseCookies = cookies(request(headerParameters(pair("COOKIE", "b=2"))));

        assertThat(lowercaseCookies.getValue("a"), is("1"));
        assertThat(uppercaseCookies.getValue("b"), is("2"));
    }

    @Test
    public void shouldCopeWithRequestCookiesInMultipleHeaders() throws Exception {
        CookieParameters cookies = cookies(request(headerParameters(pair("Cookie", "a=1"), pair("Cookie", "b=2"))));

        assertThat(cookies.getValue("a"), is("1"));
        assertThat(cookies.getValue("b"), is("2"));
    }

    @Test
    public void willIgnoreAttributesOnRequestCookiesForTheTimeBeing() throws Exception {
        CookieParameters cookies = cookies(request(headerParameters(pair("Cookie", "$Version=1; a=1; $Path=whatever; $Domain=something; b=2"))));

        assertThat(cookies.getValue("$Version"), is(nullValue()));
        assertThat(cookies.getValue("$Path"), is(nullValue()));
        assertThat(cookies.getValue("$Domain"), is(nullValue()));

        assertThat(cookies.getValue("a"), is("1"));
        assertThat(cookies.getValue("b"), is("2"));
    }

    @Test
    public void shouldCommitCookiesToResponse() throws Exception {
        CookieParameters cookies = cookies(someRequest());
        cookies.set("a", "1");
        cookies.set("b", "2");

        cookies.commit(response);

        assertThat(response.headers(SET_COOKIE_HEADER), containsInAnyOrder("a=\"1\"; ", "b=\"2\"; "));
    }

    @Test
    public void shouldAllowRollbackOfChanges() throws Exception {
        CookieParameters cookies = cookies(someRequest());
        cookies.set("a", "1");

        cookies.rollback();
        cookies.commit(response);

        assertThat(response.header(SET_COOKIE_HEADER), is(nullValue()));
    }

    @Test
    public void shouldSupportSettingCookieAttributes() throws Exception {
        CookieParameters cookies = cookies(someRequest());
        cookies.set("a", cookie("1", comment("some comment"), domain(".acme.com"), maxAge(123), path("/products"), secure(), expires(date(2010, 12, 26, 13, 16, 59))));

        cookies.commit(response);

        assertThat(
                response.header(SET_COOKIE_HEADER),
                is("a=\"1\"; Comment=\"some comment\"; Domain=\".acme.com\"; Max-Age=\"123\"; Path=\"/products\"; Secure=\"\"; Expires=\"Sun, 26-Dec-2010 13:16:59 GMT\""));
    }

    @Test
    public void shouldCorrectlyReadAndWrite() throws Exception {
        CookieParameters cookies = cookies(someRequest());
        cookies.set("a", "1");
        cookies.commit(response);

        final String value = response.header(SET_COOKIE_HEADER);
        final HeaderParameters headers = (HeaderParameters) headerParameters().add(REQUEST_COOKIE_HEADER, value);
        CookieParameters cookiesRead = cookies(request(headers));
        assertThat(cookiesRead.getValue("a"), is("1"));
    }

    @Test
    public void shouldHandleDoubleQuotesInCookieValues() throws Exception {
        assertThat(
                toHttpHeader("a", cookie("Some \"double quoted thing\"")),
                is("a=\"Some \\\"double quoted thing\\\"\"; "));
    }

    private Request someRequest() {
        return request(headerParameters());
    }

    private Request request(HeaderParameters headers) {
        return Requests.request(null, null, headers, null);
    }
}
