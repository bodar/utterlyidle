package com.googlecode.utterlyidle.cookies;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.ParametersContract;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.time.Dates.date;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.expires;
import static com.googlecode.utterlyidle.cookies.CookieParameters.cookies;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CookieParametersTest extends ParametersContract<CookieParameters> {

    @Override
    protected CookieParameters parameters() {
        return cookies(request());
    }

    @Test
    public void shouldHandleTrailingSpaces() throws Exception {
        CookieParameters cookies = cookies(request("Cookie", "a=1; ; ;"));
        assertThat(cookies.getValue("a"), is("1"));
    }

    @Test
    public void shouldBeCaseInsensitive() throws Exception {
        CookieParameters lowercaseCookies = cookies(request("cookie", "a=1"));
        CookieParameters uppercaseCookies = cookies(request("COOKIE", "b=2"));

        assertThat(lowercaseCookies.getValue("a"), is("1"));
        assertThat(uppercaseCookies.getValue("b"), is("2"));
    }

    @Test
    public void shouldCopeWithRequestCookiesInMultipleHeaders() throws Exception {
        CookieParameters cookies = cookies(request(pair("Cookie", "a=1"), pair("Cookie", "b=2")));

        assertThat(cookies.getValue("a"), is("1"));
        assertThat(cookies.getValue("b"), is("2"));
    }

    @Test
    public void willIgnoreMalformedCookies() throws Exception {
        CookieParameters cookies = cookies(request("Cookie", "invalidCookie; a=1"));

        assertThat(cookies.getValue("invalidCookie"), is(nullValue()));

        assertThat(cookies.getValue("a"), is("1"));
    }

    @Test
    public void copesWithCookieHeaderWithNoCookies() {
        CookieParameters cookies = cookies(request("Cookie", ""));
        assertThat(cookies.size(), is(0));
    }

    @Test
    public void parsesResponseCookies() {
        Response response = response(OK).cookie(cookie("test", "test value", expires(date(2013, 1, 1)))).build();

        CookieParameters cookies = CookieParameters.cookies(response);

        assertThat(cookies.getValue("test"), is("test value"));
    }

    private Request request(String name, String value) {
        return request(pair(name, value));
    }

    @SafeVarargs
    private final Request request(Pair<String, String>... headers) {
        RequestBuilder builder = RequestBuilder.get("/");
        for (Pair<String, String> header : headers) {
            builder.header(header.first(), header.second());
        }
        return builder.build();
    }
}