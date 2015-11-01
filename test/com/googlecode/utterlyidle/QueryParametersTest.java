package com.googlecode.utterlyidle;

import org.junit.Test;

import static com.googlecode.utterlyidle.QueryParameters.queryParameters;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class QueryParametersTest extends ParametersContract<QueryParameters> {
    @Override
    protected QueryParameters parameters() {
        return queryParameters();
    }

    @Test
    public void supportsCreationFromSequence() throws Exception {
        final QueryParameters original = QueryParameters.parse("foo=bar&spaz=mod");
        final QueryParameters fromSequence = QueryParameters.queryParameters(original);
        assertThat(fromSequence.toString(), is(original.toString()));
    }


    @Test
    public void supportsToString() throws Exception {
        assertThat(queryParameters().add("foo", "bar").add("foo", "bob").toString(), is("foo=bar&foo=bob"));
        assertThat(queryParameters().add("foo", "bar").add("bob", "rob").toString(), is("foo=bar&bob=rob"));
    }
}