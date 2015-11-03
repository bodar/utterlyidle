package com.googlecode.utterlyidle.cookies;

import com.googlecode.totallylazy.functions.Functions;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.HttpMessage;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.HttpMessage.Builder.cookie;
import static com.googlecode.utterlyidle.cookies.CookieCutter.cookies;
import static com.googlecode.utterlyidle.cookies.CookieEncoder.cookieEncoder;
import static com.googlecode.utterlyidle.cookies.CookieParameters.pairs;

public class EncodingCookieHandler implements HttpHandler {
    private final HttpHandler delegate;
    private final CookieEncoder encoder;

    public EncodingCookieHandler(HttpHandler delegate, CookieEncoding encoding) {
        this.delegate = delegate;
        this.encoder = cookieEncoder(encoding);
    }

    @Override
    public Response handle(Request request) throws Exception {
        return encode(delegate.handle(decode(request)));
    }

    private Response encode(Response response) {
        return Functions.modify(response, cookie(cookies(response).map(encoder::encode)));
    }

    private Request decode(Request request) {
        return Functions.modify(request, cookie(pairs(cookies(request).map(encoder::decode))));
    }
}
