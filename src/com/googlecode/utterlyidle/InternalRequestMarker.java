package com.googlecode.utterlyidle;

public class InternalRequestMarker {
    private final String applicationId;

    public InternalRequestMarker(String applicationId) {
        this.applicationId = applicationId;
    }

    public Request markAsInternal(Request request) {
        request.headers().add(HttpHeaders.X_FORWARDED_FOR, applicationId);
        return request;
    }

    public Boolean isInternal(Request request) {
        return request.headers().contains(HttpHeaders.X_FORWARDED_FOR) && request.headers().getValue(HttpHeaders.X_FORWARDED_FOR).equals(applicationId);
    }
}
