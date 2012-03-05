package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.handlers.ApplicationId;

public class InternalRequestMarker {
    private final ApplicationId applicationId;

    public InternalRequestMarker(ApplicationId applicationId) {
        this.applicationId = applicationId;
    }

    public Request markAsInternal(Request request) {
        request.headers().add(HttpHeaders.X_FORWARDED_FOR, applicationId.toString());
        return request;
    }

    public Boolean isInternal(Request request) {
        return request.headers().contains(HttpHeaders.X_FORWARDED_FOR) && request.headers().getValue(HttpHeaders.X_FORWARDED_FOR).equals(applicationId.toString());
    }
}
