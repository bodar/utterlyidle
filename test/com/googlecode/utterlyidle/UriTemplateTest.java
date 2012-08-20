package com.googlecode.utterlyidle;

import org.junit.Test;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.PathParameters.pathParameters;
import static com.googlecode.utterlyidle.UriTemplate.uriTemplate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UriTemplateTest {
    @Test
    public void reportsNumberOfSegments() throws Exception {
        assertThat(uriTemplate("properties").segments(), is(1));
        assertThat(uriTemplate("properties/{name}").segments(), is(2));
        assertThat(uriTemplate("properties/{name}/foo").segments(), is(3));
        assertThat(uriTemplate("properties/{name}/foo/{id}").segments(), is(4));
    }

    @Test
    public void ignoresPathVariablesContainingSlashes() throws Exception {
        assertThat(uriTemplate("properties/{name:foo/order}").segments(), is(2));
        assertThat(uriTemplate("properties/{name:foo/order}/foo").segments(), is(3));
        assertThat(uriTemplate("properties/{name:foo/order}/foo/{id:foo/order}").segments(), is(4));

    }

    @Test
    public void encodesOnlyPathParamsWhichDontContainForwardSlashes() throws Exception {
        UriTemplate template = uriTemplate("properties/{name}");

        assertThat(
                template.generate(pathParameters(pair("name", "a name with spaces"))),
                   is("properties/a+name+with+spaces"));

        assertThat(
                template.generate(pathParameters(pair("name", "a/name/with/slashes"))),
                   is("properties/a/name/with/slashes"));
    }

    @Test
    public void supportsMultiplePathParams() {
        UriTemplate template = uriTemplate("properties/{id}/{name}");
        assertThat(template.matches("properties/123/bob"), is(true));
        PathParameters parameters = template.extract("properties/123/bob");
        assertThat(parameters.getValue("id"), is("123"));
        assertThat(parameters.getValue("name"), is("bob"));
        assertThat(template.generate(pathParameters(pair("id", "123"), pair("name","bob"))), is("properties/123/bob"));
    }

    @Test
    public void canCaptureEnd() {
        UriTemplate template1 = uriTemplate("path");
        assertThat(template1.matches("path/123"), is(true));
        assertThat(template1.extract("path/123").getValue("$"), is("/123"));
        UriTemplate template = uriTemplate("path/{id}");
        assertThat(template.matches("path/123/someotherpath"), is(true));
        assertThat(template.extract("path/123/someotherpath").getValue("$"), is("/someotherpath"));
        assertThat(template.matches("path/123"), is(true));
        assertThat(template.generate(pathParameters(pair("id", "123"), pair("$","/someotherpath"))), is("path/123/someotherpath"));
    }

    @Test
    public void supportsCustomRegex() {
        UriTemplate template = uriTemplate("path/{id:\\d}");
        assertThat(template.matches("path/foo"), is(false));
        assertThat(template.matches("path/1"), is(true));
        assertThat(template.extract("path/1").getValue("id"), is("1"));
    }

    @Test
    public void canMatch() {
        assertThat(uriTemplate("path/{id}").matches("path/foo"), is(true));
        assertThat(uriTemplate("/path/{id}").matches("/path/foo"), is(true));
        assertThat(uriTemplate("/path/{id}/").matches("/path/foo"), is(true));
        assertThat(uriTemplate("/path/{id}/").matches("path/foo"), is(true));
        assertThat(uriTemplate("path/{id}").matches("/path/foo"), is(true));
    }

    @Test
    public void canExtractFromUri() {
        UriTemplate template = uriTemplate("path/{id}");
        assertThat(template.extract("path/foo").getValue("id"), is("foo"));
    }

    @Test
    public void canExtractFromUriWithEncodedSpace() {
        UriTemplate template = uriTemplate("path/{id1}");
        assertThat(template.extract("path/foo+bar").getValue("id1"), is("foo bar"));
    }

    @Test
    public void canGenerateUri() {
        UriTemplate template = uriTemplate("path/{id}");
        assertThat(template.generate(pathParameters(pair("id","foo"))), is("path/foo"));
    }
}
