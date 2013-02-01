package com.googlecode.utterlyidle;

import static com.googlecode.utterlyidle.HttpHeaders.X_FORWARDED_PROTO;
import static java.lang.String.format;

public enum Protocol {
    HTTP("http"),
    HTTPS("https");
    private final String schemaValue;

    Protocol(final String schemaValue) {
        this.schemaValue = schemaValue;
    }

    /** Value for use when creating urls */
    public String schemaValue() {
        return schemaValue;
    }

    public static Protocol protocol(Request request){
        if(!request.headers().contains(X_FORWARDED_PROTO))
            throw new IllegalArgumentException(format("Cannot determine protocol of request for %s without header %s", request.uri(), X_FORWARDED_PROTO));
        return protocol(request.headers().getValue(X_FORWARDED_PROTO));
    }

    public static Protocol protocol(final String value) {
        try {
            return Protocol.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(format("No protocol matches '%s'", value), e);
        }
    }
}
