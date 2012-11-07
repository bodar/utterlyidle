package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Function1;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

public class Handlers {
    public static Function1<Request, Response> asFunction(final HttpHandler httpHandler) {
        return new Function1<Request, Response>() {
            @Override
            public Response call(Request request) throws Exception {
                return httpHandler.handle(request);
            }
        };
    }
}
