package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpMessage;
import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.HttpHeaders.ACCEPT_ENCODING;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_ENCODING;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_TYPE;
import static com.googlecode.utterlyidle.HttpHeaders.VARY;
import static com.googlecode.utterlyidle.MediaType.TEXT_PLAIN;
import static com.googlecode.utterlyidle.handlers.ApplicationId.applicationId;
import static com.googlecode.utterlyidle.handlers.GZipPolicy.gZipPolicy;
import static com.googlecode.utterlyidle.handlers.GzipHandler.GZIP;
import static com.googlecode.utterlyidle.handlers.ReturnResponseHandler.returns;
import static com.googlecode.utterlyidle.sitemesh.ContentTypePredicate.contentType;
import static org.hamcrest.MatcherAssert.assertThat;

public class GzipHandlerTest {

    @Test
    public void alwaysAddsVaryHeaderSoCachesCanWorkCorrectly() throws Exception {
        GzipHandler handler = new GzipHandler(returns(Response.ok()), new InternalRequestMarker(applicationId()), gZipPolicy());

        assertThat(handler.handle(Request.get("ignored")).headers().getValue(VARY), is(ACCEPT_ENCODING));
        assertThat(handler.handle(Request.get("ignored", HttpMessage.Builder.header(ACCEPT_ENCODING, GZIP))).headers().getValue(VARY), is(ACCEPT_ENCODING));
    }

    @Test
    public void contentEncodingHeaderIsReplaced() throws Exception {
        GzipHandler handler = new GzipHandler(
                returns(Response.ok()
                        .header(CONTENT_ENCODING, "an-encoding")
                        .header(CONTENT_TYPE, TEXT_PLAIN)
                        .entity("some content")),
                new InternalRequestMarker(applicationId()), gZipPolicy().add(contentType(TEXT_PLAIN)));

        assertThat(
                handler.handle(Request.get("ignored").header(ACCEPT_ENCODING, GZIP)).headers().getValue(CONTENT_ENCODING),
                is(GZIP));
    }

}