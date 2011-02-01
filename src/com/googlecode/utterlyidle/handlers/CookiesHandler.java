package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

public class CookiesHandler implements HttpHandler{
    private HttpHandler decorated;

    public CookiesHandler(HttpHandler decorated) {
        this.decorated = decorated;
    }

    public Response handle(Request request) throws Exception {
        return request.cookies().commit(decorated.handle(request));
    }
}
