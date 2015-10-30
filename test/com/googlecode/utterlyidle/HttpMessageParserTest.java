package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.Request.Builder;
import org.junit.Test;

import static com.googlecode.totallylazy.io.Uri.uri;
import static com.googlecode.utterlyidle.HttpMessageParser.parseRequest;
import static com.googlecode.utterlyidle.HttpMessageParser.parseResponse;
import static com.googlecode.utterlyidle.HttpMessageParser.toFieldNameAndValue;
import static com.googlecode.utterlyidle.HttpMessageParser.toMethodAndPath;
import static com.googlecode.utterlyidle.HttpMessageParser.toStatus;
import static com.googlecode.utterlyidle.Request.Builder.form;
import static com.googlecode.utterlyidle.Request.Builder.get;
import static com.googlecode.utterlyidle.Response.methods.header;
import static com.googlecode.utterlyidle.Status.BAD_REQUEST;
import static com.googlecode.utterlyidle.Status.NOT_FOUND;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.Status.status;
import static com.googlecode.utterlyidle.annotations.HttpMethod.PUT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class HttpMessageParserTest {

    @Test
    public void parseRequests() {
        canParseRequest(Builder.post("/my/path",
                Builder.header("header 1", "header 1 value"),
                Builder.header("header 2", "header 2 value"),
                form("form 1", "form 1 value"),
                form("form 2", "form 2 value")));
        canParseRequest(Builder.get("/test"));
        canParseRequest(Builder.get("/test", Builder.header("name", "value")));
    }

    @Test
    public void parseRequestWithExtraSpaces() {
        Request request = HttpMessageParser.parseRequest(" PUT  /path  HTTP/1.1   \r\n  Content-Type :  text/plain \r\n\r\n body ");
        assertThat(request.method(), is(PUT));
        assertThat(request.uri().path(), is("/path"));
        assertThat(request.headers().getValue("Content-Type"), is("text/plain"));
        assertThat(request.entity().toString(), is(" body "));
    }

    private void canParseRequest(Request request) {
        assertThat(request, is(HttpMessageParser.parseRequest(request.toString())));
    }

    @Test
    public void parseRequestWithoutBodyWithoutCRLF() {
        assertThat(Request.Builder.get("/path"), is(HttpMessageParser.parseRequest("GET /path HTTP/1.1")));
    }

    @Test
    public void parseResponses() {
        canParseResponse(ResponseBuilder.response(OK).header("header name", "header value").entity("entity"));
        canParseResponse(ResponseBuilder.response(OK).entity("response"));
        canParseResponse(ResponseBuilder.response(OK));
    }

    @Test
    public void parseResponseWithoutBody() {
        String response =
                "HTTP/1.1 303 See Other\n" +
                        "Transfer-Encoding: chunked\n" +
                        "Content-Type: text/html\n" +
                        "Location: http://localhost:8899/waitrest/order\r\n\r\n";
        HeaderParameters headers = HttpMessageParser.parseResponse(response).headers();
        assertThat(headers.size(), is(3));
    }

    @Test
    public void parseResponseWithExtraSpaces() {
        Response response = HttpMessageParser.parseResponse(" HTTP/1.1  200  OK \r\n Content-Type: text/plain \r\n\r\n body ");
        assertThat(response.status(), is(OK));
        assertThat(header(response, "Content-Type"), is("text/plain"));
        assertThat(response.entity().toString(), is(" body "));
    }

    private void canParseResponse(ResponseBuilder builder) {
        Response response = builder.build();
        assertThat(response, is(HttpMessageParser.parseResponse(response.toString())));
    }

    @Test
    public void handlesHeadersParamsWithNoValue() {
        String input = get("/", Builder.header("header", "")).toString();

        Request parsed = HttpMessageParser.parseRequest(input);

        assertThat(parsed.headers().getValue("header"), is(equalTo("")));
    }

    @Test
    public void handlesFormParamsWithNoValue() {
        String input = get("/", Builder.form("form", "")).toString();

        Request parsed = HttpMessageParser.parseRequest(input);

        assertThat(Requests.form(parsed).getValue("form"), is(equalTo("")));
    }

    @Test
    public void canParseRequestWithOnlyRequestLine() {
        Request originalRequest = Request.Builder.get("/my/path");
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
        invalidResponseWithError("HTTP/1.1 \r\n\r\n", "Response without a status code");
        invalidResponseWithError("HTTP/1.0 OK\r\n\r\n", "Response without a status code");
    }

    @Test
    public void reasonPhaseIsOptional() {
        assertThat(parseResponse(ResponseBuilder.response(status(200, "")).build().toString()).status().code(), is(200));
    }

    @Test
    public void preserveNewlinesWhenParsingResponseBody() {
        Response responseWithStandardNewlines = parseResponse("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nHello\r\n\r\nJoe");
        assertThat(responseWithStandardNewlines.entity().toString(), is("Hello\r\n\r\nJoe"));

        Response responseWithUnixNewlines = parseResponse("HTTP/1.1 200 OK\nContent-Type: text/plain\n\nHello\n\nJoe");
        assertThat(responseWithUnixNewlines.entity().toString(), is("Hello\n\nJoe"));
    }

    private void invalidRequestWithError(String request, String exceptionMessage) {
        try {
            parseRequest(request);
            fail("Should not parse invalid request");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is(exceptionMessage));
        }
    }

    private void invalidResponseWithError(String response, String exceptionMessage) {
        try {
            parseResponse(response);
            fail("Should not parse invalid response");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is(exceptionMessage));
        }
    }

}
