package com.googlecode.utterlyidle;

import org.junit.Test;

import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.utterlyidle.handlers.ApplicationId.applicationId;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InternalRequestMarkerTest {
    @Test
    public void supportsMarkingARequestAsInternal() throws Exception {
        InternalRequestMarker marker = new InternalRequestMarker(applicationId());
        final Request request = marker.markAsInternal(Request.get("/testInternalUrl"));
        assertThat(marker.isInternal(request), is(true));
    }

    @Test
    public void indicatesRequestIsNotInternalForRequestWithNoForwardedForHeader() throws Exception {
        InternalRequestMarker marker = new InternalRequestMarker(applicationId());
        assertThat(marker.isInternal(Request.get("/testInternalUrl")), is(false));
    }

    @Test
    public void indicatedRequestIsNotInternalForForwardedForValueNotEqualToApplicationId() throws Exception {
        InternalRequestMarker marker = new InternalRequestMarker(applicationId());
        assertThat(marker.isInternal(Request.get("/testInternalUrl", HttpMessage.Builder.header("X-Forwarded-For", randomUUID()))), is(false));
    }

    @Test
    public void shouldWorkWhenXForwardedForAlreadyInRequest() throws Exception {
        Request request = Request.get("/", HttpMessage.Builder.header(HttpHeaders.X_FORWARDED_FOR, "quidgeybo"));
        InternalRequestMarker marker = new InternalRequestMarker(applicationId());
        Request internalRequest = marker.markAsInternal(request);
        assertThat(marker.isInternal(internalRequest), is(true));
    }
}
