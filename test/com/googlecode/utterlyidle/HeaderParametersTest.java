package com.googlecode.utterlyidle;

import org.junit.Test;

import java.util.HashMap;

import static com.googlecode.totallylazy.Arrays.list;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class HeaderParametersTest extends ParametersContract<HeaderParameters> {
    @Test
    public void ignoresNullHeaderKeys() throws Exception {
        HashMap<String, Iterable<String>> source = new HashMap<String, Iterable<String>>() {{
            put(null, list("value"));
            put("Happy", list("value"));
        }};
        assertThat(source.size(), is(2));
        HeaderParameters headerParameters = headerParameters(source);
        assertThat(headerParameters.size(), is(1));
    }

    @Override
    protected HeaderParameters parameters() {
        return headerParameters();
    }
}
