package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Sequences;
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
        assertThat(clonedRequest.uri(), is(equalTo(originalRequest.uri())));
        assertThat(clonedRequest.headers().toString(), is(equalTo(originalRequest.headers().toString())));
        assertThat(new String(clonedRequest.input()), is(equalTo(new String(originalRequest.input()))));

    }
}

