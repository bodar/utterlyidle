package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import org.junit.Assert;
import org.junit.Test;

import static com.googlecode.utterlyidle.HttpMessageParser.*;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.Status.*;
import static com.googlecode.utterlyidle.annotations.HttpMethod.GET;
import static com.googlecode.utterlyidle.annotations.HttpMethod.POST;
import static com.googlecode.utterlyidle.io.Url.url;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class HttpMessageParserTest {

    @Test
    public void parseRequests() {
        canParseRequest(new RequestBuilder(POST, "/my/path").
                withHeader("header 1", "header 1 value").
                withHeader("header 2", "header 2 value").
                withForm("form 1", "form 1 value").
                withForm("form 2", "form 2 value").
                build());
        canParseRequest(new RequestBuilder(GET, "/test").build());
        canParseRequest(new RequestBuilder(GET, "/test").withHeader("name", "value").build());
    }

    private void canParseRequest(Request request) {
        assertThat(request, is(HttpMessageParser.parseRequest(request.toString())));
    }

    @Test
    public void parseRequestWithoutBodyWithoutCRLF() {
        assertThat(new RequestBuilder(GET, "/path").build(), is(HttpMessageParser.parseRequest("GET /path HTTP/1.1")));
    }

    @Test
    public void parseResponses() {
        canParseResponse(response(OK).header("header name", "header value").bytes("entity".getBytes()));
        canParseResponse(Responses.response(Status.OK).bytes("response".getBytes()));
        canParseResponse(Responses.response(Status.OK));
    }

    private void canParseResponse(Response response) {
        assertThat(response, is(HttpMessageParser.parseResponse(response.toString())));
    }

    @Test
    public void parseResponseWithoutBodyWithoutCRLF() {
        assertThat(Responses.response(status(426, "Upgrade Required")),
                is(HttpMessageParser.parseResponse("HTTP/1.1 426 Upgrade Required")));
    }

    @Test
    public void handlesHeadersParamsWithNoValue() {
        String input = get("/").withHeader("header", "").build().toString();

        Request parsed = HttpMessageParser.parseRequest(input);

        assertThat(parsed.headers().getValue("header"), is(equalTo("")));
    }

    @Test
    public void handlesFormParamsWithNoValue() {
        String input = get("/").withForm("form", "").build().toString();

        Request parsed = HttpMessageParser.parseRequest(input);

        assertThat(parsed.form().getValue("form"), is(equalTo("")));
    }

    @Test
    public void canParseRequestWithOnlyRequestLine() {
        RequestBuilder builder = new RequestBuilder(GET, "/my/path");
        Request originalRequest = builder.build();

        Request parsedRequest = HttpMessageParser.parseRequest(originalRequest.toString());

        assertThat(parsedRequest.method(), is(equalTo("GET")));
        assertThat(parsedRequest.url(), is(equalTo(url("/my/path"))));
    }

    @Test
    public void parseResponseStatusLine() {
        assertThat(toStatus("HTTP/1.1 404 Not Found"), is(NOT_FOUND));
        assertThat(toStatus("HTTP/1.0 400 Bad Request"), is(BAD_REQUEST));
    }

    @Test
    public void parseRequestLine() {
        assertThat(toMethodAndPath("GET http://localhost:8080/path/ HTTP/1.1"), is(Pair.<String, String>pair("GET", "http://localhost:8080/path/")));
    }

    @Test
    public void parseHeader() {
        assertThat(toFieldNameAndValue("Accept: text/xml"), is(Pair.<String, String>pair("Accept", "text/xml")));
    }

    @Test
    public void invalidRequestParsingErrors() {
        invalidRequestWithError("", "Http Message without a start line");
        invalidRequestWithError("GET HTTP/1.1", "Request without a path");
        invalidRequestWithError("/test HTTP/1.1", "Request without a method");
    }

    @Test
    public void invalidResponseParsingErrors() {
        invalidResponseWithError("HTTP/1.1 ", "Response without a status code");
        invalidResponseWithError("HTTP/1.0 OK", "Response without a status code");
    }

    @Test
    public void reasonPhaseIsOptional() {
        assertThat(parseResponse("Http/1.1 200").status().code(), is(200));
    }

    private void invalidRequestWithError(String request, String exceptionMessage) {
        try {
            parseRequest(request);
            fail("Should not parse invalid request");
        } catch(IllegalArgumentException e) {
            assertThat(e.getMessage(), is(exceptionMessage));
        }
    }

    private void invalidResponseWithError(String response, String exceptionMessage) {
        try {
            parseResponse(response);
            fail("Should not parse invalid response");
        } catch(IllegalArgumentException e) {
            assertThat(e.getMessage(), is(exceptionMessage));
        }
    }

}
