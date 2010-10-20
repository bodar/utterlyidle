package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Redirect;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;

public class RedirectHandler implements ResponseHandler<Redirect> {
    private final Request request;

    public RedirectHandler(Request request) {
        this.request = request;
    }

    public void handle(Redirect redirect, Response response) {
        redirect.applyTo(request.base(), response);
    }
}
