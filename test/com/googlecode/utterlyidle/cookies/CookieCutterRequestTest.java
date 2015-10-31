package com.googlecode.utterlyidle.cookies;

import com.googlecode.utterlyidle.HttpMessage;
import com.googlecode.utterlyidle.Request;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.HttpHeaders.COOKIE;
import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.utterlyidle.HttpMessage.Builder.header;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.cookies.CookieCutter.cookies;
import static com.googlecode.utterlyidle.cookies.EmptyCookiesMatcher.isEmpty;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class CookieCutterRequestTest {

    private final Cookie cookie1 = cookie("cookie1", "Viscount");
    private final Cookie cookie2 = cookie("cookie2", "Penguin");
    private final Cookie cookie3 = cookie("cookie3", "Club");

    @Test
    public void handlesRequestsWithNoCookies() throws Exception {
        assertThat(cookies(requestWithoutCookies()), isEmpty());
    }

    @Test
    public void extractsCookiesFromRequest() throws Exception {
        assertThat(cookies(requestWithCookies(cookie1, cookie2, cookie3)), containsInAnyOrder(cookie1, cookie2, cookie3));
    }

    @Test
    public void ignoresMalformedCookies() throws Exception {
        Request request = Request.get("/", HttpMessage.Builder.header(COOKIE, "hello;" + cookie1));
        assertThat(cookies(request), contains(cookie1));
    }

    @Test
    public void shouldHandleDoubleQuotesInCookieValues() throws Exception {
        Cookie cookie = cookie("a", "\"");
        MatcherAssert.assertThat(cookies(requestWithCookies(cookie)), contains(cookie));
    }

    private Request requestWithoutCookies() {
        return requestWithCookies();
    }

    private Request requestWithCookies(Cookie... cookies) {
        return Request.get("/", Request.Builder.cookie(CookieParameters.pairs(sequence(cookies))));
    }
}
