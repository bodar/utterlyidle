package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.Status;

import java.io.IOException;

import static com.googlecode.utterlyidle.ResponseBuilder.modify;

public class NoContentHandler implements ResponseHandler {
    public Response handle(Response response) throws IOException {
        if (Status.OK.equals(response.status())) {
            return modify(response).status(Status.NO_CONTENT).build();
        }
        return response;
    }
}
