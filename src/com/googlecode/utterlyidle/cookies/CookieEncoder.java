package com.googlecode.utterlyidle.cookies;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.cookies.CookieBuilder.modify;
import static com.googlecode.utterlyidle.cookies.CookieCutter.cookies;

public class CookieEncoder implements HttpHandler {

    private final HttpHandler delegate;
    private final CookieEncoding encoder;

    public CookieEncoder(HttpHandler delegate, CookieEncoding encoder) {
        this.delegate = delegate;
        this.encoder = encoder;
    }

    @Override
    public Response handle(Request request) throws Exception {
        return encode(delegate.handle(decode(request)));
    }

    private Response encode(Response response) {
        final ResponseBuilder builder = ResponseBuilder.modify(response);
        sequence(cookies(response)).fold(builder, new Callable2<ResponseBuilder, Cookie, ResponseBuilder>() {
            @Override
            public ResponseBuilder call(ResponseBuilder builder, Cookie cookie) throws Exception {
                return builder.replaceCookie(modify(cookie).value(encoder.encode(cookie.value())).build());
            }
        });
        return builder.build();
    }

    private Request decode(Request request) {
        final RequestBuilder builder = RequestBuilder.modify(request);
        sequence(cookies(request)).fold(builder, new Callable2<RequestBuilder, Cookie, RequestBuilder>() {
            @Override
            public RequestBuilder call(RequestBuilder builder, Cookie cookie) throws Exception {
                return builder.replaceCookie(modify(cookie).value(encoder.decode(cookie.value())).build());
            }
        });
        return builder.build();
    }
}
