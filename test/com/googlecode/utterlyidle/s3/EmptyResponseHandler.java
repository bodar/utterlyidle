package com.googlecode.utterlyidle.s3;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.Status.OK;

public class EmptyResponseHandler implements HttpHandler {
    @Override
    public Response handle(final Request request) throws Exception {
        return response(OK).build();
    }
}
