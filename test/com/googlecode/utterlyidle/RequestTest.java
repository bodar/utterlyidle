package com.googlecode.utterlyidle;

import org.junit.Test;

import static com.googlecode.utterlyidle.RequestBuilder.post;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RequestTest {
    @Test
    public void shouldBeReversibleToRawMessage() {
        assertThat(post("http://www.youtube.com/watch?v=606eK4abteQ")
                .accepting("text/html")
                .withForm("chups", "nah bru")
                .withForm("plinkton", "nom")
                .withHeader("Cookie", "size=diciptive")
                .withHeader("Referer", "http://google.com").
                        build().toString(),
                   is(
                           "POST http://www.youtube.com/watch?v=606eK4abteQ HTTP/1.1\n" +
                                   "Accept: text/html\n" +
                                   "Content-Type: application/x-www-form-urlencoded\n" +
                                   "Cookie: size=diciptive\n" +
                                   "Referer: http://google.com\n" +
                                   "Content-length: 26\n" +
                                   "\n" +
                                   "chups=nah+bru&plinkton=nom"
                   ));
    }
}
