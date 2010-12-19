package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.*;
import com.googlecode.yadic.Resolver;

import javax.ws.rs.core.HttpHeaders;

public class RedirectHandler implements ResponseHandler<Redirect> {
    private final BasePath basePath;

    public RedirectHandler(BasePath basePath) {
        this.basePath = basePath;
    }

    public void handle(Redirect redirect, Resolver resolver, Response response) {
        response.status(Status.SEE_OTHER);
        response.header(HttpHeaders.LOCATION, basePath.file(redirect.location()).toString());
    }
}
