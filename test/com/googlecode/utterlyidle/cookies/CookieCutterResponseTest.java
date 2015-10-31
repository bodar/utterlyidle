package com.googlecode.utterlyidle.cookies;

import com.googlecode.utterlyidle.HttpMessage;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import org.junit.Test;

import static com.googlecode.utterlyidle.HttpHeaders.SET_COOKIE;
import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.utterlyidle.HttpMessage.Builder.header;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.comment;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.httpOnly;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.secure;
import static com.googlecode.utterlyidle.cookies.CookieCutter.cookies;
import static com.googlecode.utterlyidle.cookies.EmptyCookiesMatcher.isEmpty;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class CookieCutterResponseTest {

    private final Cookie cookie1 = cookie("cookie1", "Viscount");
    private final Cookie cookie2 = cookie("cookie2", "Penguin", httpOnly(), secure());
    private final Cookie cookie3 = cookie("cookie3", "Club", comment("If you like a lot of chocolate"));

    @Test
    public void handlesRequestsWithNoCookies() throws Exception {
        assertThat(cookies(responseWithoutCookies()), isEmpty());
    }

    @Test
    public void extractsCookiesFromRequest() throws Exception {
        assertThat(cookies(responseWithCookies(cookie1, cookie2, cookie3)), containsInAnyOrder(cookie1, cookie2, cookie3));
    }

    @Test
    public void ignoresMalformedCookies() throws Exception {
        Request request = Request.get("/", HttpMessage.Builder.header(SET_COOKIE, "hello; Path=/malformed;"));
        assertThat(cookies(request), isEmpty());
    }

    private Response responseWithoutCookies() {
        return responseWithCookies();
    }

    private Response responseWithCookies(Cookie... cookies) {
        ResponseBuilder builder = ResponseBuilder.response();
        for (Cookie cookie : cookies) {
            builder.cookie(cookie);
        }
        return builder.build();
    }
}