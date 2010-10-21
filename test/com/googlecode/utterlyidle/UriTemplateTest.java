package com.googlecode.utterlyidle;

import org.junit.Test;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.PathParameters.pathParameters;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UriTemplateTest {
    @Test
    public void supportedMultiplePathParams() {
        UriTemplate template = new UriTemplate("properties/{id}/{name}");
        assertThat(template.matches("properties/123/bob"), is(true));
        PathParameters parameters = template.extract("properties/123/bob");
        assertThat(parameters.getValue("id"), is("123"));
        assertThat(parameters.getValue("name"), is("bob"));
        assertThat(template.generate(pathParameters(pair("id", "123"), pair("name","bob"))), is("properties/123/bob"));
    }

    @Test
    public void canCaptureEnd() {
        UriTemplate template = new UriTemplate("path/{id}");
        assertThat(template.matches("path/123/someotherpath"), is(true));
        assertThat(template.extract("path/123/someotherpath").getValue("$"), is("/someotherpath"));
        assertThat(template.matches("path/123"), is(true));
        assertThat(template.generate(pathParameters(pair("id", "123"), pair("$","/someotherpath"))), is("path/123/someotherpath"));
    }

    @Test
    public void supportsCustomRegex() {
        UriTemplate template = new UriTemplate("path/{id:\\d}");
        assertThat(template.matches("path/foo"), is(false));
        assertThat(template.matches("path/1"), is(true));
        assertThat(template.extract("path/1").getValue("id"), is("1"));
    }

    @Test
    public void canMatch() {
        UriTemplate template = new UriTemplate("path/{id}");
        assertThat(template.matches("path/foo"), is(true));
    }

    @Test
    public void canExtractFromUri() {
        UriTemplate template = new UriTemplate("path/{id}");
        assertThat(template.extract("path/foo").getValue("id"), is("foo"));
    }

    @Test
    public void canGenerateUri() {
        UriTemplate template = new UriTemplate("path/{id}");
        assertThat(template.generate(pathParameters(pair("id","foo"))), is("path/foo"));
    }
}
