package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.InternalRequestMarker;
import org.junit.Test;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.HttpHeaders.ACCEPT_ENCODING;
import static com.googlecode.utterlyidle.HttpHeaders.VARY;
import static com.googlecode.utterlyidle.Request.Builder.get;
import static com.googlecode.utterlyidle.Request.Builder.header;
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

        assertThat(handler.handle(get("ignored")).headers().getValue(VARY), is(ACCEPT_ENCODING));
        assertThat(handler.handle(get("ignored", header(ACCEPT_ENCODING, GZIP))).headers().getValue(VARY), is(ACCEPT_ENCODING));
    }

}