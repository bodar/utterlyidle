package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.Redirect;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.yadic.Resolver;

public class RedirectHandler implements ResponseHandler<Redirect> {
    private final BasePath basePath;

    public RedirectHandler(BasePath basePath) {
        this.basePath = basePath;
    }

    public void handle(Redirect redirect, Resolver resolver, Response response) {
        redirect.applyTo(basePath, response);
    }
}
