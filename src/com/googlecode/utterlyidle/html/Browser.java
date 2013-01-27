package com.googlecode.utterlyidle.html;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.ClientHttpHandler;
import com.googlecode.utterlyidle.handlers.HttpClient;
import com.googlecode.utterlyidle.handlers.RedirectHttpHandler;

@Deprecated
/** Superseded by com.googlecode.utterlyidle.html.Browser */
public class Browser implements HttpClient {
    private final HttpHandler httpHandler;

    private Browser(HttpHandler httpHandler) {
        this.httpHandler = new RedirectHttpHandler(new RelativeUrlHandler(httpHandler));
    }

    public static Browser browser() {
        return new Browser(new ClientHttpHandler());
    }

    public static Browser browser(Application application) {
        return new Browser(application);
    }

    @Override
    public Response handle(Request request) throws Exception {
        return httpHandler.handle(request);
    }
}
