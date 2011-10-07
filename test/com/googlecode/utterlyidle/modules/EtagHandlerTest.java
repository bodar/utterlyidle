package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import org.junit.Test;

import static com.googlecode.utterlyidle.HttpHeaders.Content_MD5;
import static com.googlecode.utterlyidle.HttpHeaders.ETAG;
import static com.googlecode.utterlyidle.HttpHeaders.IF_NONE_MATCH;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.Status.NO_CONTENT;
import static com.googlecode.utterlyidle.handlers.ReturnResponseHandler.returnsResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EtagHandlerTest {
    @Test
    public void calculatesStrongEtagWhichMustBeQuoted() throws Exception{
        HttpHandler handler = new EtagHandler(returnsResponse(response().bytes("abc".getBytes("UTF-8"))));
        Response response = handler.handle(get("/").build());
        assertThat(response.header(ETAG), is("\"900150983cd24fb0d6963f7d28e17f72\""));
    }

    @Test
    public void setsContentMD5ForGoodMeasure() throws Exception{
        HttpHandler handler = new EtagHandler(returnsResponse(response().bytes("abc".getBytes("UTF-8"))));
        Response response = handler.handle(get("/").build());
        assertThat(response.header(Content_MD5), is("kAFQmDzST7DWlj99KOF/cg=="));
    }

    @Test
    public void onlyAppliesEtagToGetRequests() throws Exception{
        HttpHandler handler = new EtagHandler(returnsResponse(response().bytes("abc".getBytes("UTF-8"))));
        Response response = handler.handle(post("/").build());
        assertThat(response.headers().contains(ETAG), is(false));
        assertThat(response.headers().contains(Content_MD5), is(false));
    }

    @Test
    public void returnsNotModifiedIfEtagMatches() throws Exception{
        HttpHandler handler = new EtagHandler(returnsResponse(response().bytes("abc".getBytes("UTF-8"))));
        Response response = handler.handle(get("/").withHeader(IF_NONE_MATCH, "\"900150983cd24fb0d6963f7d28e17f72\"").build());
        assertThat(response.status(), is(Status.NOT_MODIFIED));
        assertThat(response.bytes().length, is(0));
    }

    @Test
    public void supportsNoContent() throws Exception{
        HttpHandler handler = new EtagHandler(returnsResponse(response(NO_CONTENT)));
        Response response = handler.handle(get("/").build());
        assertThat(response.header(ETAG), is("\"d41d8cd98f00b204e9800998ecf8427e\""));
        assertThat(response.header(Content_MD5), is("1B2M2Y8AsgTpgAmY7PhCfg=="));

    }
}
