package com.googlecode.utterlyidle;

public class Responses {
    public static Response response() {
        return new MemoryResponse();
    }

    public static Response response(Status status) {
        return new MemoryResponse().status(status);
    }

    public static Response response(Status status, HeaderParameters headers, Object entity) {
        return new MemoryResponse(status, headers, entity);
    }

    public static Response seeOther(String location) {
        return response(Status.SEE_OTHER).header(HttpHeaders.LOCATION, location);
    }

}
