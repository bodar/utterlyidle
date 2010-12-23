package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.*;

import javax.ws.rs.core.HttpHeaders;

public class RedirectHandler implements ResponseHandler {
    private final BasePath basePath;

    public RedirectHandler(BasePath basePath) {
        this.basePath = basePath;
    }

    public void handle(Response response) {
        Redirect redirect = (Redirect) response.entity();
        applyTo(response, redirect, basePath);
    }

    public static void applyTo(final Response response, final Redirect redirect, final BasePath basePath) {
        response.status(Status.SEE_OTHER);
        response.header(HttpHeaders.LOCATION, basePath.file(redirect.location()).toString());
    }
}
