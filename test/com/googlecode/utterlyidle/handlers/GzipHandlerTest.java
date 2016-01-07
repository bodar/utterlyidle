package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.sitemesh.ContentTypePredicate;
import org.junit.Test;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.HttpHeaders.ACCEPT_ENCODING;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_ENCODING;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_TYPE;
import static com.googlecode.utterlyidle.HttpHeaders.VARY;
import static com.googlecode.utterlyidle.MediaType.TEXT_PLAIN;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.handlers.ApplicationId.applicationId;
import static com.googlecode.utterlyidle.handlers.GZipPolicy.gZipPolicy;
import static com.googlecode.utterlyidle.handlers.GzipHandler.GZIP;
import static com.googlecode.utterlyidle.handlers.ReturnResponseHandler.returns;
import static com.googlecode.utterlyidle.sitemesh.ContentTypePredicate.contentType;
import static org.hamcrest.MatcherAssert.assertThat;

public class GzipHandlerTest {

    @Test
    public void alwaysAddsVaryHeaderSoCachesCanWorkCorrectly() throws Exception {
        GzipHandler handler = new GzipHandler(returns(response()), new InternalRequestMarker(applicationId()), gZipPolicy());

        assertThat(handler.handle(get("ignored").build()).headers().getValue(VARY), is(ACCEPT_ENCODING));
        assertThat(handler.handle(get("ignored").header(ACCEPT_ENCODING, GZIP).build()).headers().getValue(VARY), is(ACCEPT_ENCODING));
    }

    @Test
    public void contentEncodingHeaderIsReplaced() throws Exception {
        GzipHandler handler = new GzipHandler(
                returns(ResponseBuilder.response()
                        .header(CONTENT_ENCODING, "an-encoding")
                        .header(CONTENT_TYPE, TEXT_PLAIN)
                        .entity("some content")
                        .build()),
                new InternalRequestMarker(applicationId()), gZipPolicy().add(contentType(TEXT_PLAIN)));

        assertThat(
                handler.handle(get("ignored").header(ACCEPT_ENCODING, GZIP).build()).headers().getValue(CONTENT_ENCODING),
                is(GZIP));
    }

}