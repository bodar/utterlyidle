package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;

import java.util.List;

import static com.googlecode.totallylazy.Sequences.sequence;

public class ResponseBuilder {
    private Status status;
    private List<Pair<String, String>> headers;
    private Object entity;

    public ResponseBuilder(Status status, Iterable<Pair<String, String>> headers, Object entity) {
        this.status = status;
        this.headers = sequence(headers).toList();
        this.entity = entity;
    }

    public static ResponseBuilder modify(Response response) {
        return new ResponseBuilder(response.status(), response.headers(), response.entity());
    }

    public ResponseBuilder status(Status status) {
        this.status = status;
        return this;
    }

    public Response build(){
        return Responses.response(status, HeaderParameters.headerParameters(headers), entity);
    }
}
