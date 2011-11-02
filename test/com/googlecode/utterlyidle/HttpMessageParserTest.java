package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import org.junit.Test;

import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.utterlyidle.HttpMessageParser.*;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.Status.*;
import static com.googlecode.utterlyidle.annotations.HttpMethod.GET;
import static com.googlecode.utterlyidle.annotations.HttpMethod.POST;
import static com.googlecode.utterlyidle.annotations.HttpMethod.PUT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
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

    @Test
    public void parseRequestWithExtraSpaces() {
        Request request = HttpMessageParser.parseRequest(" PUT  /path  HTTP/1.1   \r\n  Content-Type :  text/plain \r\n\r\n body ");
        assertThat(request.method(), is(PUT));
        assertThat(request.uri().path().toString(), is("/path"));
        assertThat(request.headers().getValue("Content-Type"), is("text/plain"));
        assertThat(new String(request.input()), is(" body "));
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
        canParseResponse(Responses.response(OK).bytes("response".getBytes()));
        canParseResponse(Responses.response(OK));
    }

    @Test
    public void parseResponseWithoutBodyAndWithoutSeparatorLine() {
        String response =
                "HTTP/1.1 303 See Other\n" +
                "Transfer-Encoding: chunked\n" +
                "Content-Type: text/html\n" +
                "Location: http://localhost:8899/waitrest/order";
        HeaderParameters headers = HttpMessageParser.parseResponse(response).headers();
        assertThat(headers.size(), is(3));
    }

    @Test
    public void parseResponseWithExtraSpaces() {
        Response response = HttpMessageParser.parseResponse(" HTTP/1.1  200  OK \r\n Content-Type: text/plain \r\n\r\n body ");
        assertThat(response.status(), is(OK));
        assertThat(response.header("Content-Type"), is("text/plain"));
        assertThat(new String(response.bytes()), is(" body "));
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

        assertThat(Requests.form(parsed).getValue("form"), is(equalTo("")));
    }

    @Test
    public void canParseRequestWithOnlyRequestLine() {
        RequestBuilder builder = new RequestBuilder(GET, "/my/path");
        Request originalRequest = builder.build();

        Request parsedRequest = HttpMessageParser.parseRequest(originalRequest.toString());

        assertThat(parsedRequest.method(), is(equalTo("GET")));
        assertThat(parsedRequest.uri(), is(equalTo(uri("/my/path"))));
    }

    @Test
    public void parseResponseStatusLine() {
        assertThat(toStatus("HTTP/1.1 404 Not Found"), is(NOT_FOUND));
        assertThat(toStatus("HTTP/1.0 400 Bad Request"), is(BAD_REQUEST));
    }

    @Test
    public void uppercaseLowercaseMethods() {
        assertThat(toMethodAndPath("testExtensionMethod http://localhost:8080/path/ HTTP/1.1"), is(Pair.<String, String>pair("TESTEXTENSIONMETHOD", "http://localhost:8080/path/")));
        assertThat(toMethodAndPath("get http://localhost:8080/path/ HTTP/1.1"), is(Pair.<String, String>pair("GET", "http://localhost:8080/path/")));
    }

    @Test
    public void parseHeader() {
        assertThat(toFieldNameAndValue("Accept: text/xml"), is(Pair.<String, String>pair("Accept", "text/xml")));
        assertThat(toFieldNameAndValue("Location: http://localhost:8899/waitrest/order"), is(Pair.<String, String>pair("Location", "http://localhost:8899/waitrest/order")));
    }

    @Test
    public void invalidRequestParsingErrors() {
        invalidRequestWithError("", "Http Message without a start line");
        invalidRequestWithError("GET HTTP/1.1", "Request without a path");
        invalidRequestWithError("/test HTTP/1.1", "Request without a valid method");
    }

    @Test
    public void invalidResponseParsingErrors() {
        invalidResponseWithError("HTTP/1.1 ", "Response without a status code");
        invalidResponseWithError("HTTP/1.0 OK", "Response without a status code");
    }

    @Test
    public void reasonPhaseIsOptional() {
        assertThat(parseResponse("HTTP/1.1 200").status().code(), is(200));
    }

    @Test
    public void preserveSpacesWhenParsingResponse() {
        assertEquals(parseResponse("HTTP/1.1 200 OK").toString(), response(OK).toString());
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
