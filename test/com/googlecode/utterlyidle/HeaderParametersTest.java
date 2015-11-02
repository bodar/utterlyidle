package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Assert;
import org.junit.Test;

import java.util.HashMap;

import static com.googlecode.totallylazy.Arrays.head;
import static com.googlecode.totallylazy.Arrays.list;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.HttpHeaders.ACCEPT;
import static com.googlecode.utterlyidle.MediaType.APPLICATION_JSON;
import static com.googlecode.utterlyidle.MediaType.APPLICATION_XML;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

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

    @Test
    public void toStringMatchesRfc2616() throws Exception {
        assertEquals(headerParameters().toString(), "");
        assertEquals(headerParameters(pair(ACCEPT, APPLICATION_JSON)).toString(), "Accept: application/json\r\n");
        assertEquals(headerParameters(pair(ACCEPT, APPLICATION_JSON), pair(ACCEPT, APPLICATION_XML)).toString(), "Accept: application/json\r\nAccept: application/xml\r\n");
    }
}
