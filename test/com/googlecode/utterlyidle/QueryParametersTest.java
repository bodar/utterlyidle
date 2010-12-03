package com.googlecode.utterlyidle;

import org.junit.Test;

import static com.googlecode.utterlyidle.QueryParameters.queryParameters;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class QueryParametersTest {
    @Test
    public void supportsToString() throws Exception {
        assertThat(queryParameters().add("foo", "bar").add("foo", "bob").toString(), is("?foo=bar&foo=bob"));
        assertThat(queryParameters().add("foo", "bar").add("bob", "rob").toString(), is("?foo=bar&bob=rob"));
    }
}
