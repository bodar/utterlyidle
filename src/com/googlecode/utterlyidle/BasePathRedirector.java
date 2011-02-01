package com.googlecode.utterlyidle;

import javax.ws.rs.core.HttpHeaders;

import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.Status.SEE_OTHER;
import static javax.ws.rs.core.HttpHeaders.LOCATION;

public class BasePathRedirector implements Redirector{
    private final BasePath basePath;

    public BasePathRedirector(BasePath basePath) {
        this.basePath = basePath;
    }

    public Response redirect(String location) {
        return response().status(SEE_OTHER).header(LOCATION, basePath.file(location).toString());
    }
}
