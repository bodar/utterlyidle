package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.*;
import org.junit.Test;

import static com.googlecode.utterlyidle.HttpHeaders.*;
import static com.googlecode.utterlyidle.Request.Builder.get;
import static com.googlecode.utterlyidle.Request.Builder.post;
import static com.googlecode.utterlyidle.Response.methods.header;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.Status.SEE_OTHER;
import static com.googlecode.utterlyidle.handlers.ReturnResponseHandler.returnsResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EtagHandlerTest {
    @Test
    public void onlyPassesThroughSafeHeaders() throws Exception{
        HttpHandler handler = new EtagHandler(returnsResponse(ResponseBuilder.response(OK).entity("abc").header(HttpHeaders.DATE, "passesThrough").header("X-foo", "doesntPassThrough").build()));
        Response response = handler.handle(get("/", Request.Builder.header(IF_NONE_MATCH, "\"900150983cd24fb0d6963f7d28e17f72\"")));

        assertThat(response.headers().contains("X-foo"), is(false));
        assertThat(header(response, HttpHeaders.DATE), is("passesThrough"));
    }

    @Test
    public void calculatesStrongEtagWhichMustBeQuoted() throws Exception{
        HttpHandler handler = new EtagHandler(returnsResponse(response().entity("abc")));
        Response response = handler.handle(get("/"));
        assertThat(header(response, ETAG), is("\"900150983cd24fb0d6963f7d28e17f72\""));
    }

    @Test
    public void setsContentMD5ForGoodMeasure() throws Exception{
        HttpHandler handler = new EtagHandler(returnsResponse(response().entity("abc")));
        Response response = handler.handle(get("/"));
        assertThat(header(response, Content_MD5), is("kAFQmDzST7DWlj99KOF/cg=="));
    }

    @Test
    public void onlyAppliesEtagToGetRequestsWithOkResponse() throws Exception{
        HttpHandler handler = new EtagHandler(returnsResponse(response(OK).entity("abc")));
        Response response = handler.handle(post("/"));
        assertThat(response.headers().contains(ETAG), is(false));
        assertThat(response.headers().contains(Content_MD5), is(false));

        HttpHandler handler1 = new EtagHandler(returnsResponse(response(SEE_OTHER).entity("abc")));
        Response response1 = handler1.handle(get("/"));
        assertThat(response1.headers().contains(ETAG), is(false));
        assertThat(response1.headers().contains(Content_MD5), is(false));
    }

    @Test
    public void returnsNotModifiedIfEtagMatches() throws Exception{
        HttpHandler handler = new EtagHandler(returnsResponse(response().entity("abc")));
        Response response = handler.handle(get("/", Request.Builder.header(IF_NONE_MATCH, "\"900150983cd24fb0d6963f7d28e17f72\"")));
        assertThat(response.status(), is(Status.NOT_MODIFIED));
        assertThat(response.entity().toString().length(), is(0));
    }
}
