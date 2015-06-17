package com.googlecode.utterlyidle.cookies;

import com.googlecode.totallylazy.Function2;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.cookies.CookieCutter.cookies;
import static com.googlecode.utterlyidle.cookies.CookieEncoder.*;

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
        ResponseBuilder builder = ResponseBuilder.modify(response);
        sequence(cookies(response)).fold(builder, new Function2<ResponseBuilder, Cookie, ResponseBuilder>() {
            @Override
            public ResponseBuilder call(ResponseBuilder builder, Cookie cookie) throws Exception {
                return builder.replaceCookie(encoder.encode(cookie));
            }
        });
        return builder.build();
    }

    private Request decode(Request request) {
        RequestBuilder builder = RequestBuilder.modify(request);
        sequence(cookies(request)).fold(builder, new Function2<RequestBuilder, Cookie, RequestBuilder>() {
            @Override
            public RequestBuilder call(RequestBuilder builder, Cookie cookie) throws Exception {
                return builder.replaceCookie(encoder.decode(cookie));
            }
        });
        return builder.build();
    }
}
