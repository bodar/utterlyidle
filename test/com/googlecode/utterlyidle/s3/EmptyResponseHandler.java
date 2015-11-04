package com.googlecode.utterlyidle.s3;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

public class EmptyResponseHandler implements HttpHandler {
    @Override
    public Response handle(final Request request) throws Exception {
        return Response.ok();
    }
}
