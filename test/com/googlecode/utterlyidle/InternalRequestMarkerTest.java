package com.googlecode.utterlyidle;

import org.junit.Test;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InternalRequestMarkerTest {

    @Test
    public void supportsMarkingARequestAsInternal() throws Exception {
        InternalRequestMarker internalRequestMarker = new InternalRequestMarker("application id");

        final Request request = internalRequestMarker.markAsInternal(RequestBuilder.get("/testInternalUrl").build());

        assertThat(internalRequestMarker.isInternal(request), is(true));
    }

    @Test
    public void indicatesRequestIsNotInternalForRequestWithNoForwardedForHeader() throws Exception {
        InternalRequestMarker internalRequestMarker = new InternalRequestMarker("application id");

        assertThat(internalRequestMarker.isInternal(RequestBuilder.get("/testInternalUrl").build()), is(false));
    }

    @Test
    public void indicatedRequestIsNotInternalForForwardedForValueNotEqualToApplicationId() throws Exception {
        InternalRequestMarker internalRequestMarker = new InternalRequestMarker("application id");

        assertThat(internalRequestMarker.isInternal(RequestBuilder.get("/testInternalUrl").withHeader("X-Forwarded-For", randomUUID()).build()), is(false));
    }
}
