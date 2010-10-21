package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Redirect;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.yadic.Resolver;

public class RedirectHandler implements ResponseHandler<Redirect> {
    private final Request request;

    public RedirectHandler(Request request) {
        this.request = request;
    }

    public void handle(Redirect redirect, Resolver resolver, Response response) {
        redirect.applyTo(request.base(), response);
    }
}
