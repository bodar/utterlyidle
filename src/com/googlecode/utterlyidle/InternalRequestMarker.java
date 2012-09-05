package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.handlers.ApplicationId;

import static com.googlecode.utterlyidle.RequestBuilder.modify;

public class InternalRequestMarker {
    private final ApplicationId applicationId;

    public InternalRequestMarker(ApplicationId applicationId) {
        this.applicationId = applicationId;
    }

    public Request markAsInternal(Request request) {
        return modify(request).header(HttpHeaders.X_FORWARDED_FOR, applicationId.toString()).build();
    }

    public Boolean isInternal(Request request) {
        return request.headers().contains(HttpHeaders.X_FORWARDED_FOR) && request.headers().getValue(HttpHeaders.X_FORWARDED_FOR).equals(applicationId.toString());
    }
}
