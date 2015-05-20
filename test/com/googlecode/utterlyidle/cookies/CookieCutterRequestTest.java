package com.googlecode.utterlyidle.cookies;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import org.junit.Test;

import static com.googlecode.utterlyidle.HttpHeaders.COOKIE;
import static com.googlecode.utterlyidle.RequestBuilder.get;
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
        Request request = get("/").header(COOKIE, "hello;" + cookie1).build();
        assertThat(cookies(request), contains(cookie1));
    }

    private Request requestWithoutCookies() {
        return requestWithCookies();
    }

    private Request requestWithCookies(Cookie... cookies) {
        RequestBuilder builder = RequestBuilder.get("/");
        for (Cookie cookie : cookies) {
            builder.cookie(cookie);
        }
        return builder.build();
    }
}