package com.googlecode.utterlyidle;

public class Responses {
    public static Response response() {
        return new MemoryResponse();
    }

    public static Response response(Status status, HeaderParameters headers, Object entity) {
        return new MemoryResponse(status, headers, entity);
    }
}
