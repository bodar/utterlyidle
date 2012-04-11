package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Uri;
import org.junit.Test;

import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RequestBuilderTest {
    @Test
    public void canCreateRequestBuilderFromRequest() {
        Request originalRequest = new RequestBuilder("GET", "/home").
                cookie("fred", cookie("blogs")).
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
}