package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.annotations.HttpMethod;
import org.junit.Test;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.io.Url.url;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RequestParserTest {

    @Test
    public void canParseFullyPopulatedRequest() {
        RequestBuilder builder = new RequestBuilder(HttpMethod.POST, "/my/path");
        builder.withHeader("header 1", "header 1 value");
        builder.withHeader("header 2", "header 2 value");
        builder.withForm("form 1", "form 1 value");
        builder.withForm("form 2", "form 2 value");
        Request originalRequest = builder.build();

        Request parsedRequest = new RequestParser().parse(originalRequest.toString()).build();

        assertThat(parsedRequest.method(), is(equalTo("POST")));
        assertThat(parsedRequest.url(), is(equalTo(url("/my/path"))));
        assertThat(parsedRequest.headers().getValue("header 1"), is(equalTo("header 1 value")));
        assertThat(parsedRequest.headers().getValue("header 2"), is(equalTo("header 2 value")));
        assertThat(parsedRequest.form().getValue("form 1"), is(equalTo("form 1 value")));
        assertThat(parsedRequest.form().getValue("form 2"), is(equalTo("form 2 value")));
    }

    @Test
    public void handlesHeadersParamsWithNoValue() {
        String input = get("/").withHeader("header", "").build().toString();
        Request parsed = new RequestParser().parse(input).build();
        assertThat(parsed.headers().getValue("header"), is(equalTo("")));
    }

    @Test
    public void handlesFormParamsWithNoValue() {
        String input = get("/").withForm("form", "").build().toString();
        Request parsed = new RequestParser().parse(input).build();
        assertThat(parsed.form().getValue("form"), is(equalTo("")));
    }

    @Test
    public void canParseRequestWithOnlyRequestLine() {
        RequestBuilder builder = new RequestBuilder(HttpMethod.GET, "/my/path");
        Request originalRequest = builder.build();

        Request parsedRequest = new RequestParser().parse(originalRequest.toString()).build();

        assertThat(parsedRequest.method(), is(equalTo("GET")));
        assertThat(parsedRequest.url(), is(equalTo(url("/my/path"))));
    }
}
