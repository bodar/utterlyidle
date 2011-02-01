package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Redirect;
import com.googlecode.utterlyidle.Redirector;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;

public class RedirectHandler implements ResponseHandler{
    private final Redirector redirector;

    public RedirectHandler(Redirector redirector) {
        this.redirector = redirector;
    }

    public Response handle(Response response) throws Exception {
        Redirect redirect = (Redirect) response.entity();
        return redirector.redirect(redirect);
    }
}
