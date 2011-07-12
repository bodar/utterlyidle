package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;

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

    public static Callable1<Response, Status> status() {
        return new Callable1<Response, Status>() {
            public Status call(Response response) throws Exception {
                return response.status();
            }
        };
    }

}
