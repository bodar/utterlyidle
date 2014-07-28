package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.cookies.CookieParameters;

public class CookieHandler implements HttpHandler {
    private final HttpHandler httpHandler;
    private CookieParameters cookies = CookieParameters.cookies();

    public CookieHandler(final HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        return updateCookiesReceived(httpHandler.handle(addExistingCookiesTo(request)));
    }

    private Request addExistingCookiesTo(final Request request) {
        RequestBuilder builder = RequestBuilder.modify(request);
        for (Pair<String, String> pair : cookies) {
            builder.cookie(pair.first(), pair.second());
        }
        return builder.build();
    }

    private Response updateCookiesReceived(final Response response) {
        for (Pair<String, String> pair : CookieParameters.cookies(response)) {
            cookies = cookies.replace(pair.first(), pair.second());
        }
        return response;
    }
}
