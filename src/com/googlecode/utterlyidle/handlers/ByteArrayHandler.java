package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;

public class ByteArrayHandler implements ResponseHandler {
    public Response handle(Response response) throws Exception {
        byte[] bytes = (byte[]) response.entity();
        return response.bytes(bytes);
    }
}
