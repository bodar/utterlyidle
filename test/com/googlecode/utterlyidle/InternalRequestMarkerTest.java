package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.Request.Builder;
import org.junit.Test;

import static com.googlecode.utterlyidle.Request.Builder.get;
import static com.googlecode.utterlyidle.handlers.ApplicationId.applicationId;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InternalRequestMarkerTest {
    @Test
    public void supportsMarkingARequestAsInternal() throws Exception {
        InternalRequestMarker marker = new InternalRequestMarker(applicationId());
        final Request request = marker.markAsInternal(get("/testInternalUrl"));
        assertThat(marker.isInternal(request), is(true));
    }

    @Test
    public void indicatesRequestIsNotInternalForRequestWithNoForwardedForHeader() throws Exception {
        InternalRequestMarker marker = new InternalRequestMarker(applicationId());
        assertThat(marker.isInternal(get("/testInternalUrl")), is(false));
    }

    @Test
    public void indicatedRequestIsNotInternalForForwardedForValueNotEqualToApplicationId() throws Exception {
        InternalRequestMarker marker = new InternalRequestMarker(applicationId());
        assertThat(marker.isInternal(get("/testInternalUrl", Builder.header("X-Forwarded-For", randomUUID()))), is(false));
    }

    @Test
    public void shouldWorkWhenXForwardedForAlreadyInRequest() throws Exception {
        Request request = Request.Builder.get("/", Request.Builder.header(HttpHeaders.X_FORWARDED_FOR, "quidgeybo"));
        InternalRequestMarker marker = new InternalRequestMarker(applicationId());
        Request internalRequest = marker.markAsInternal(request);
        assertThat(marker.isInternal(internalRequest), is(true));
    }
}
