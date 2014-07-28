package com.googlecode.utterlyidle.html;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.ClientHttpHandler;
import com.googlecode.utterlyidle.handlers.HttpClient;
import com.googlecode.utterlyidle.handlers.RedirectHttpHandler;

public class Browser implements HttpClient {
    private final HttpHandler httpHandler;

    protected Browser(HttpHandler httpHandler) {
        this.httpHandler = new RedirectHttpHandler(new RelativeUrlHandler(new CookieHandler(httpHandler)));
    }

    public static Browser browser() {
        return new Browser(new ClientHttpHandler());
    }

    public static Browser browser(Application handler) {
        return new Browser(handler);
    }

    @Override
    public Response handle(Request request) throws Exception {
        return httpHandler.handle(request);
    }
}
