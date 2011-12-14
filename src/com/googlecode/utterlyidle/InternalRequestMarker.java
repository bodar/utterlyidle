package com.googlecode.utterlyidle;

public class InternalRequestMarker {

    private static final String FORWARDED_FOR_HEADER = "X-Forwarded-For";
    private final String applicationId;

    public InternalRequestMarker(String applicationId) {
        this.applicationId = applicationId;
    }

    public Request markAsInternal(Request request) {
        request.headers().add(FORWARDED_FOR_HEADER, applicationId);
        return request;
    }

    public Boolean isInternal(Request request) {
        return request.headers().contains(FORWARDED_FOR_HEADER) && request.headers().getValue(FORWARDED_FOR_HEADER).equals(applicationId);
    }
}
