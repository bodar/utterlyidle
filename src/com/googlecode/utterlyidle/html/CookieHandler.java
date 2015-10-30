package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.cookies.CookieParameters;

import static com.googlecode.utterlyidle.Request.Builder.cookie;
import static com.googlecode.utterlyidle.Request.Builder.modify;

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
        return modify(request, cookie(cookies));
    }

    private Response updateCookiesReceived(final Response response) {
        for (Pair<String, String> pair : CookieParameters.cookies(response)) {
            cookies = cookies.replace(pair.first(), pair.second());
        }
        return response;
    }
}
