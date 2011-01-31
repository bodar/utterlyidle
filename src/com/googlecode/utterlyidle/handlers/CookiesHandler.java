package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.cookies.Cookies;

public class CookiesHandler implements HttpHandler{
    private final Cookies cookies;
    private HttpHandler decorated;

    public CookiesHandler(Cookies cookies, HttpHandler decorated) {
        this.cookies = cookies;
        this.decorated = decorated;
    }

    public Response handle(Request request) throws Exception {
        return cookies.commit(decorated.handle(request));
    }
}
