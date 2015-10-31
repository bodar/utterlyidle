package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpMessage;
import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.Request;
import org.junit.Test;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.HttpHeaders.ACCEPT_ENCODING;
import static com.googlecode.utterlyidle.HttpHeaders.VARY;
import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.utterlyidle.HttpMessage.Builder.header;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.handlers.ApplicationId.applicationId;
import static com.googlecode.utterlyidle.handlers.GZipPolicy.gZipPolicy;
import static com.googlecode.utterlyidle.handlers.GzipHandler.GZIP;
import static com.googlecode.utterlyidle.handlers.ReturnResponseHandler.returns;
import static org.hamcrest.MatcherAssert.assertThat;

public class GzipHandlerTest {
    @Test
    public void alwaysAddsVaryHeaderSoCachesCanWorkCorrectly() throws Exception {
        GzipHandler handler = new GzipHandler(returns(response()), new InternalRequestMarker(applicationId()), gZipPolicy());

        assertThat(handler.handle(Request.get("ignored")).headers().getValue(VARY), is(ACCEPT_ENCODING));
        assertThat(handler.handle(Request.get("ignored", HttpMessage.Builder.header(ACCEPT_ENCODING, GZIP))).headers().getValue(VARY), is(ACCEPT_ENCODING));
    }

}