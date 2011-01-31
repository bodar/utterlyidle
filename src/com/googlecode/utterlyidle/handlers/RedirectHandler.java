package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.*;

import javax.ws.rs.core.HttpHeaders;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.Status.SEE_OTHER;
import static javax.ws.rs.core.HttpHeaders.LOCATION;

public class RedirectHandler implements ResponseHandler {
    private final BasePath basePath;

    public RedirectHandler(BasePath basePath) {
        this.basePath = basePath;
    }

    public Response handle(Response response) {
        SeeOther entity = (SeeOther) response.entity();
        return response.status(SEE_OTHER).header(LOCATION, basePath.file(entity.location()).toString());
    }
}
