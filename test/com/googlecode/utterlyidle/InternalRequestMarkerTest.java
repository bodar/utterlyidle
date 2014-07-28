package com.googlecode.utterlyidle;

import org.junit.Test;

import static com.googlecode.utterlyidle.handlers.ApplicationId.applicationId;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InternalRequestMarkerTest {
    @Test
    public void supportsMarkingARequestAsInternal() throws Exception {
        InternalRequestMarker marker = new InternalRequestMarker(applicationId());
        final Request request = marker.markAsInternal(RequestBuilder.get("/testInternalUrl").build());
        assertThat(marker.isInternal(request), is(true));
    }

    @Test
    public void indicatesRequestIsNotInternalForRequestWithNoForwardedForHeader() throws Exception {
        InternalRequestMarker marker = new InternalRequestMarker(applicationId());
        assertThat(marker.isInternal(RequestBuilder.get("/testInternalUrl").build()), is(false));
    }

    @Test
    public void indicatedRequestIsNotInternalForForwardedForValueNotEqualToApplicationId() throws Exception {
        InternalRequestMarker marker = new InternalRequestMarker(applicationId());
        assertThat(marker.isInternal(RequestBuilder.get("/testInternalUrl").header("X-Forwarded-For", randomUUID()).build()), is(false));
    }

    @Test
    public void shouldWorkWhenXForwardedForAlreadyInRequest() throws Exception {
        Request request = RequestBuilder.get("/").header(HttpHeaders.X_FORWARDED_FOR, "quidgeybo").build();
        InternalRequestMarker marker = new InternalRequestMarker(applicationId());
        Request internalRequest = marker.markAsInternal(request);
        assertThat(marker.isInternal(internalRequest), is(true));
    }
}
