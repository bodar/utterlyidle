package com.googlecode.utterlyidle;

import org.junit.Test;

import javax.ws.rs.core.HttpHeaders;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.matchers.IterableMatcher.hasExactly;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static org.hamcrest.MatcherAssert.assertThat;

public class HeaderParametersTest {
    @Test
    public void shouldBeAbleToRemoveAllParametersByName() {
        assertThat(
                sequence(headerParameters(
                        pair(HttpHeaders.ACCEPT, "text/html"),
                        pair(HttpHeaders.ACCEPT_LANGUAGE, "en-us"),
                        pair(HttpHeaders.ACCEPT, "text/plain")).
                        remove(HttpHeaders.ACCEPT)),
                hasExactly(pair(HttpHeaders.ACCEPT_LANGUAGE, "en-us")));
    }
}
