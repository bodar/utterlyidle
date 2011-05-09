package com.googlecode.utterlyidle;

import org.junit.Test;

import static com.googlecode.utterlyidle.cookies.Cookie.cookie;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RequestBuilderTest {

    @Test
    public void canCreateRequestBuilderFromRequest() {
        Request originalRequest = new RequestBuilder("GET", "/home")
                .withCookie("fred", cookie("blogs"))
                .withForm("going", "well")
                .withHeader("some", "header")
                .accepting("accept header")
                .withQuery("a query", "a question").build();

        Request clonedRequest = new RequestBuilder(originalRequest).build();

        assertThat(clonedRequest.method(), is(equalTo(originalRequest.method())));
        assertThat(clonedRequest.form().toString(), is(equalTo(originalRequest.form().toString())));
        assertThat(clonedRequest.headers().toString(), is(equalTo(originalRequest.headers().toString())));
        assertThat(clonedRequest.query().toString(), is(equalTo(originalRequest.query().toString())));
        assertThat(clonedRequest.url(), is(equalTo(originalRequest.url())));
        assertThat(clonedRequest.cookies().toString(), is(equalTo(originalRequest.cookies().toString())));

    }
}

