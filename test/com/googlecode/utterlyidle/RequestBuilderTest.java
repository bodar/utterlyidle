package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.cookies.Cookie;
import com.googlecode.utterlyidle.cookies.CookieParameters;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.bytes;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_LENGTH;
import static com.googlecode.utterlyidle.HttpHeaders.COOKIE;
import static com.googlecode.utterlyidle.Requests.form;
import static com.googlecode.utterlyidle.Requests.query;
import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static com.googlecode.utterlyidle.cookies.CookieParameters.toHttpHeader;
import static java.lang.String.valueOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RequestBuilderTest {
    @Test
    public void alwaysSetsContentLengthForNonStreamingEntity() throws Exception {
        assertThat(RequestBuilder.get("/home").entity("Hello").build().headers().getValue(CONTENT_LENGTH), equalTo(valueOf(bytes("Hello").length)));
        assertThat(RequestBuilder.get("/home").entity(bytes("Hello")).build().headers().getValue(CONTENT_LENGTH), equalTo(valueOf(bytes("Hello").length)));
        assertThat(RequestBuilder.get("/home").entity(new ByteArrayInputStream(bytes("Hello"))).build().headers().contains(CONTENT_LENGTH), equalTo(false));
        assertThat(RequestBuilder.get("/home").build().headers().contains(CONTENT_LENGTH), equalTo(false));
    }

    @Test
    public void canCreateRequestBuilderFromRequest() {
        Request originalRequest = new RequestBuilder("GET", "/home").
                cookie(cookie("fred", "blogs")).
                form("going", "well").
                header("some", "header").
                accepting("accept header").
                query("a query", "a question").
                build();

        Request clonedRequest = new RequestBuilder(originalRequest).build();

        assertThat(clonedRequest.method(), is(equalTo(originalRequest.method())));
        assertThat(clonedRequest.uri(), is(equalTo(originalRequest.uri())));
        assertThat(clonedRequest.headers().toString(), is(equalTo(originalRequest.headers().toString())));
        assertThat(clonedRequest.entity().toString(), is(equalTo(originalRequest.entity().toString())));
    }

    @Test
    public void shouldRemoveQueryParamsFromEncodedUri() throws Exception {
        RequestBuilder requestBuilder = new RequestBuilder("GET", "/home").query("^&%$^%", "foo").query("removeme", "");
        assertThat(requestBuilder.removeQuery("removeme").build().uri(), equalTo(Uri.uri("/home?%5E%26%25%24%5E%25=foo")));
    }

    @Test
    public void shouldBeAbleToReplaceACookie() throws Exception {
        Request request = RequestBuilder.get("/").cookie("cookie1", "value1").cookie("cookie2", "value2").replaceCookie("cookie1", "timtam").build();
        assertThat(CookieParameters.cookies(request.headers()).getValue("cookie1"), is("timtam"));
    }

    @Test
    public void shouldBeAbleToReplaceACookieEvenIfHeaderParameterCaseDiffers() throws Exception {
        Request request = RequestBuilder.get("/").
                header(COOKIE.toLowerCase(), toHttpHeader(Cookie.cookie("cookie1", "McVitees Digestive with caramel"))).
                cookie("cookie2", "value2").
                replaceCookie("cookie1", "timtam").build();

        assertThat(CookieParameters.cookies(request.headers()).getValue("cookie1"), is("timtam"));
    }

    @Test
    public void replacingACookiePreservesHeaderOrder() throws Exception {
        Request request = RequestBuilder.get("/").header("path", "/").cookie("cookie1", "value1").cookie("cookie2", "value2").replaceCookie("cookie2", "penguin").build();
        assertThat(request.headers(), is(headerParameters(sequence(pair("path", "/"), pair(COOKIE, "cookie1=\"value1\""), pair(COOKIE, "cookie2=\"penguin\"")))));
    }

    @Test
    public void canReplaceCookieWhenListedInMiddleOfMultiCookie() throws Exception {
        Request request = RequestBuilder.get("/").header(COOKIE, "cookie1=\"value1\"; cookie2=\"value2\"").replaceCookie("cookie2", "hobnob").build();
        assertThat(request.headers().toMap().get(COOKIE).get(0), is("cookie1=\"value1\"; cookie2=\"hobnob\""));
    }

    @Test
    public void canCopyFormParamsIntoQueryParams() {
        Request postForm = RequestBuilder.post("/?three=3").form("one", "1").form("two", 2).build();

        Request modified = RequestBuilder.modify(postForm).copyFormParamsToQuery().build();

        QueryParameters queryParameters = query(modified);
        assertThat(queryParameters.getValue("one"), is(equalTo("1")));
        assertThat(queryParameters.getValue("two"), is(equalTo("2")));
        assertThat(queryParameters.getValue("three"), is(equalTo("3")));
        FormParameters formParameters = form(modified);
        assertThat(formParameters.size(), is(2));
    }
}